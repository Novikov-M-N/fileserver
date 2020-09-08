import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileTransferHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if (message instanceof FileTransferMessage) {
            System.out.println("FileTransferMessage was recieved");
        }
    }
}
