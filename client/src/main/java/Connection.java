import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

public class Connection {

    private Socket socket;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private String login = "";
    private String password = "";
    private LoginCases loginCase;
    private List<FileMetadata> serverList;
    private MainController mainController;

    //Набор флагов для отслеживания наличия ответов от сервера
    private Map<String, Boolean> flags = new HashMap<>();

    public void send(Packet packet) throws IOException {    //Отправка пакета и логирование
        System.out.println("-> " + packet);
        os.writeObject(packet);
    }

    /**
     * Пауза для того, чтобы подождать ответ от сервера
     * @param stepTimeout сколько миллисекунд ждать на каждом шаге ожидания
     * @param stepCount количество шагов ожидания
     * @param flag флаг получения ответа от сервера: если true, то ответ получен,
     *             ожидание можно прерывать
     * @throws InterruptedException
     */
    private void wait(long stepTimeout, int stepCount, String flag) throws InterruptedException {
        for (int i = 0; i < stepCount; i++) {
            Thread.sleep(stepTimeout);
            if (flags.get(flag)) { break; }
        }
    }

    /**
     * Корректный разрыв соединения - отправка серверу запроса на отключение
     */
    public void stop() {
        try {
            send(new Packet(Commands.disconnect));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection(String host, int port, MainController mainController) {
        try {
            socket = new Socket(host,port);
            System.out.println("socket");
            is = new ObjectDecoderInputStream(socket.getInputStream());
            System.out.println("input stream");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            System.out.println("output stream");
            //Создаём и запускаем обработчик входящих пакетов
            InputProcessor inputProcessor = new InputProcessor(this, is);
            new Thread(inputProcessor).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mainController = mainController;
    }

    /**
     * Запрос авторизации: проверка корректности логина и пароля, введённых в форме авторизации
     * @param login Логин, под которым желает войти пользователь
     * @param password Пароль, введённый пользователем
     * @return Ответ сервера: одобрение авторизации либо отказ с указанием причины
     */
    public LoginCases checkLoginPassword(String login, String password) {
        this.password = password;
        loginCase = LoginCases.SERVER_NOT_RESPOND;//Заранее предполагаем, что сервер не ответил
        flags.put("loginAnswerFlag",false);
        //Отдельная переменная нужна на случай, если между проверкой ответа сервера
        //и выдачей результата функции изменится значение поля loginCase
        LoginCases lc;
        try {
            send(new Packet(Commands.authrequest)
                    .addParam("-login", login));//Отправляем на сервер запрос на авторизацию
            //Несколько попыток прочитать ответ сервера с промежутком в полсекунды
            wait(500,20,"loginAnswerFlag");
            lc = loginCase;
            if (lc == LoginCases.ACCESS_IS_ALLOWED) {
                send(new Packet(Commands.loginconfirm));
                this.login = login;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LoginCases.UNKNOWN_ERROR;//Сообщаем пользователю об ошибке
        } catch (InterruptedException e) {
            e.printStackTrace();
            return LoginCases.UNKNOWN_ERROR;//Сообщаем пользователю об ошибке
        }
        return lc;
    }

    public List<FileMetadata> getServerList() {
        serverList = new ArrayList<>();
        flags.put("fileListFlag",false);
        try {
            send(new Packet(Commands.ls));//Запрос от сервера списка файлов в текущей директории
            wait(200,40,"fileListFlag");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return serverList;
    }

    public void changeCurrentDirectory(String directory) {
        flags.put("changeDirectoryFlag", false);
        try {
            send(new Packet(Commands.cd).addParam("-directory", directory));
            wait(200, 40,"changeDirectoryFlag");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void copyFile(String fileName) {
        try {
            send(new Packet(Commands.cp).addParam("-name",fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработчик команд, получаемых во входящих пакетах
     * @param command
     * @param params
     */
    public void commandExecutor(Commands command, Map<String, Object> params) {
        switch (command) {
            case passwrequestcode:
                //В ответ на код запроса от сервера вычисляем ответный код и отправляем на сервер
                int passwordConfirmCode = Security.passwordConfirmCode(password,(int)params.get("-code"));
                try {
                    send(new Packet(Commands.passwconfirmcode)
                        .addParam("-code", passwordConfirmCode));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case loginanswer:
                //Ответ от сервера по авторизации заносим в поле loginCase
                loginCase = (LoginCases)params.get("-answ");
                flags.put("loginAnswerFlag",true);
                break;
            case disconnect:
                //Если сервер прислал команду на разрыв соединения, закрываем потоки и сокет
                try {
                    os.close();
                    is.close();
                    socket.close();
                    System.out.println("disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case filelist:
                serverList.addAll((List<FileMetadata>)params.get("-files"));
                flags.put("fileListFlag",true);
                break;
            case cd_ok:
                flags.put("changeDirectoryFlag", true);
                break;
            case bytedata:
                String name = (String) params.get("-name");
                byte[] data = (byte[]) params.get("-data");
                this.mainController.getFileManager().writeToFile(name, data, true);
                break;
        }
    }

}
