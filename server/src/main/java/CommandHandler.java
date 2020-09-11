import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CommandHandler extends SimpleChannelInboundHandler<Packet> {
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        if (packet instanceof Packet) {
            System.out.println("Command message was recieved");
        } else {
            channelHandlerContext.writeAndFlush(packet);
        }

    }
}
