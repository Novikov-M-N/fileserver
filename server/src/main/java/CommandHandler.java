import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CommandHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if (message instanceof CommandMessage) {
            System.out.println("Command message was recieved");
        }

    }
}
