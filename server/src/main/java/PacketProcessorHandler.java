import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class PacketProcessorHandler extends SimpleChannelInboundHandler<Packet> {
    private Server server;
    private int clientNumber;
    private int passwordRequestCode;
    private int passwordConfirmCode;
    private String requestLogin = "";//Логин, под которым клиент попросился войти
    private String login = "";//Логин, который был присвоен клиенту после одобрения авторизации
    private ServerFileManager serverFileManager;//Экземпляр класса, управляющего файлами в хранилище

    private void log(String message) {
        System.out.println("Client #" + clientNumber + " (" + login + "): " + message);
    }

    private void send(ChannelHandlerContext ctx, Packet packet) {
        ctx.writeAndFlush(packet);
        log("-> " + packet.toString());
    }

    public PacketProcessorHandler(Server server, int clientNumber) {
        this.server = server;
        this.clientNumber = clientNumber;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.rmLogin(login);
        log("disconnected");
        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet packet) throws Exception {
        log("<- " + packet.toString());
        Commands command = packet.getCommand();
        switch (command) {
            case authrequest:
                requestLogin = (String)packet.getParam("-login");
                if (server.loginExist(requestLogin)) {
                    send(ctx,new Packet(Commands.loginanswer)
                                .addParam("-answ",LoginCases.USER_IS_ALREADY_LOGGED_IN));
                } else {
                    Random random = new Random();
                    passwordRequestCode = random.nextInt(1023) + 1;
                    passwordConfirmCode = Security.passwordConfirmCode("admin", passwordRequestCode);
                    send(ctx, new Packet(Commands.passwrequestcode)
                                    .addParam("-code", passwordRequestCode));
                }
                break;
            case passwconfirmcode:
                if ((int)packet.getParam("-code") == passwordConfirmCode) {
                    send(ctx, new Packet(Commands.loginanswer)
                                    .addParam("-answ", LoginCases.ACCESS_IS_ALLOWED));
                } else {
                    log("invalid password confirm code from client");
                    send(ctx, new Packet(Commands.loginanswer)
                                    .addParam("-answ", LoginCases.WRONG_LOGIN_PASSWORD));
                }
                break;
            case loginconfirm:
                login = requestLogin;
                server.addLogin(login);
                serverFileManager = new ServerFileManager(login);
                log("logged successful");
                break;
            case disconnect:
                send(ctx, new Packet(Commands.disconnect));
                break;
            case ls:
                send(ctx, new Packet(Commands.filelist)
                        .addParam("-files", serverFileManager.getFileList()));
                break;
            default:
                log("unknown command");
                break;
        }
    }
}
