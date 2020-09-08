import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class LoginRequestHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if (message instanceof LoginRequestMessage) {
            String login = ((LoginRequestMessage) message).getLogin();
            System.out.println(login);
            Random random = new Random();
            int requestCode = random.nextInt(1023)+1;
            channelHandlerContext.writeAndFlush(
                    new PasswordRequestMessage(requestCode));
        }
    }
}
