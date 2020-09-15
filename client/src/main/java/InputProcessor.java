import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import java.io.IOException;
import java.util.Map;

public class InputProcessor implements Runnable{
    private Connection connection;
    private ObjectDecoderInputStream is;
    private boolean isConnected;

    public InputProcessor(Connection connection, ObjectDecoderInputStream is) {
        this.connection = connection;
        this.is = is;
        this.isConnected = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = (Packet) is.readObject();
                System.out.println("<<< " + packet);
                Commands commmand = packet.getCommand();
                Map<String, Object> params = packet.getParams();
                connection.commandExecutor(commmand, params);
                if (commmand == Commands.disconnect) { break; }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
