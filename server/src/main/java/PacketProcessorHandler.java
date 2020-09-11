import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class PacketProcessorHandler extends SimpleChannelInboundHandler<Packet> {
    private Server server;
    private int clientNumber;
    private int passwordRequestCode = 0;
    private int passwordConfirmCode = 0;
    private String requestLogin = "";
    private String login = "";

    public void log(String message) {
        System.out.println("Client #" + clientNumber + " (" + login + "): " + message);
    }

    public PacketProcessorHandler(Server server, int clientNumber) {
        this.server = server;
        this.clientNumber = clientNumber;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet packet) throws Exception {
        log(packet.toString());
        Commands command = packet.getCommand();
        switch (command) {
            case authrequest:
                requestLogin = (String)packet.getParam("-login");
                if (server.loginExist(requestLogin)) {
                    ctx.writeAndFlush(new Packet(Commands.loginanswer)
                                .addParam("-answ",LoginCases.USER_IS_ALREADY_LOGGED_IN));
                } else {
                    Random random = new Random();
                    passwordRequestCode = random.nextInt(1023) + 1;
                    log("Password request code from server: " + passwordRequestCode);
                    passwordConfirmCode = Security.passwordConfirmCode("admin", passwordRequestCode);
                    log("Password confirm code from server: " + passwordConfirmCode);
                    ctx.writeAndFlush(
                            new Packet(Commands.passwrequestcode).addParam("-code", passwordRequestCode));
                }
                break;
            case passwconfirmcode:
                if ((int)packet.getParam("-code") == passwordConfirmCode) {
                    login = requestLogin;
                    server.addLogin(login);
                    ctx.writeAndFlush(
                            new Packet(Commands.loginanswer)
                                    .addParam("-answ", LoginCases.ACCESS_IS_ALLOWED));
                } else {
                    log("invalid password confirm code from client");
                    ctx.writeAndFlush(
                            new Packet(Commands.loginanswer)
                                    .addParam("-answ", LoginCases.WRONG_LOGIN_PASSWORD));
                }
                break;
            default:
                log("unknown command");
                break;
        }
    }
}
