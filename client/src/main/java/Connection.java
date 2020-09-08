import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

public class Connection {
    static Socket socket;
    static ObjectDecoderInputStream inputStream;
    static ObjectEncoderOutputStream outputStream;

    public Connection(String host, int port) {
        try {
            socket = new Socket(host,port);
            inputStream = new ObjectDecoderInputStream(socket.getInputStream());
            outputStream = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkLoginPassword(String login, String password) {
        try {
            outputStream.writeObject(new LoginRequestMessage(login));
            PasswordRequestMessage passwordRequestMessage = (PasswordRequestMessage) inputStream.readObject();
            System.out.println(passwordRequestMessage.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return login.equals("admin") && password.equals("admin");
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
}
