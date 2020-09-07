import java.util.ArrayList;

public class Connection {

    public Connection(String host, int port) {
        System.out.println("Connected on " + host + ":" + port);
    }

    public static boolean checkLoginPassword(String login, String password) {
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
