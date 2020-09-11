import io.netty.handler.codec.serialization.ObjectDecoderInputStream;

import java.io.IOException;
import java.util.Map;

public class InputProcessor implements Runnable{
    private Connection connection;
    private ObjectDecoderInputStream is;

    public InputProcessor(Connection connection, ObjectDecoderInputStream is) {
        this.connection = connection;
        this.is = is;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = (Packet) is.readObject();
                if (packet instanceof Packet) {
                    System.out.println(packet);
                    Commands commmand = ((Packet) packet).getCommand();
                    Map<String, Object> params = ((Packet) packet).getParams();
                    connection.commandExecutor(commmand,params);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
