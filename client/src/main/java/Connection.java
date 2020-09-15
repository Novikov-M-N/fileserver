import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

public class Connection {
    private Socket socket;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private InputProcessor inputProcessor;
    private String login = "";
    private String password = "";
    private LoginCases loginCase;

    public InputProcessor getInputProcessor() { return inputProcessor; }

    public void send(Packet packet) throws IOException {
        System.out.println(">>> " + packet);
        os.writeObject(packet);
    }

    public void stop() {
        try {
            send(new Packet(Commands.disconnect));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection(String host, int port) {
        try {
            socket = new Socket(host,port);
            System.out.println("socket");
            is = new ObjectDecoderInputStream(socket.getInputStream());
            System.out.println("input stream");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            System.out.println("output stream");
            this.inputProcessor = new InputProcessor(this,is);
            new Thread(inputProcessor).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoginCases checkLoginPassword(String login, String password) {
        this.password = password;
        loginCase = LoginCases.SERVER_NOT_RESPOND;
        try {
            send(new Packet(Commands.authrequest)
                    .addParam("-login", login));
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
            return LoginCases.UNKNOWN_ERROR;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return LoginCases.UNKNOWN_ERROR;
        }
        return loginCase;
    }

    public ArrayList<String> getServerList() {
        ArrayList<String> serverList = new ArrayList();
        serverList.add("serverFile1");
        serverList.add("serverFile2");
        serverList.add("serverFile3");
        serverList.add("serverFile4");
        return serverList;
    }

    public ArrayList<String> getClientList() {
        ArrayList<String> clientList = new ArrayList();
        clientList.add("clientFile1");
        clientList.add("clientFile2");
        clientList.add("clientFile3");
        clientList.add("clientFile4");
        return clientList;
    }

    public void commandExecutor(Commands command, Map<String, Object> params) {
        switch (command) {
            case passwrequestcode:
                int passwordConfirmCode = Security.passwordConfirmCode(password,(int)params.get("-code"));
                try {
                    send(new Packet(Commands.passwconfirmcode)
                        .addParam("-code", passwordConfirmCode));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case loginanswer:
                loginCase = (LoginCases)params.get("-answ");
                break;
            case disconnect:
                try {
                    os.close();
                    is.close();
                    socket.close();
                    System.out.println("disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
