import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

public class Connection {
    static Socket socket;
    static ObjectDecoderInputStream is;
    static ObjectEncoderOutputStream os;
    private String login = "";
    private static String password = "";
    private static LoginCases loginCase;

    public Connection(String host, int port) {
        try {
            socket = new Socket(host,port);
            System.out.println("socket");
            is = new ObjectDecoderInputStream(socket.getInputStream());
            System.out.println("input stream");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            System.out.println("output stream");
            new Thread(new InputProcessor(this, is)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LoginCases checkLoginPassword(String login, String password) {
        loginCase = LoginCases.SERVER_NOT_RESPOND;
        Connection.password = password;
        try {
            os.writeObject(new Packet(Commands.authrequest)
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
                int passwordRequestCode = (int)params.get("-code");
                int passwordConfirmCode = Security.passwordConfirmCode(password,passwordRequestCode);
                try {
                    os.writeObject(new Packet(Commands.passwconfirmcode)
                        .addParam("-code", passwordConfirmCode));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case loginanswer:
                LoginCases answer = (LoginCases)params.get("-answ");
                Connection.loginCase = answer;
                break;

        }
    }
}
