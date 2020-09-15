import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.ArrayList;

public class Server {
    private Server server;
    private int clientCounter = 0;
    private ArrayList<String> logins;

    public void addLogin(String login) {this.logins.add(login);}
    public void rmLogin(String login) {this.logins.remove(login);}
    public boolean loginExist(String login) {return logins.contains(login);}

    public static void log(String message) {
        System.out.println(message);
    };

    public Server() {
        this.server = this;
        logins = new ArrayList<String>();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            if (clientCounter == Integer.MAX_VALUE) {
                                clientCounter = 1;
                            } else {
                                clientCounter++;
                            }
                            int clientNumber = clientCounter;
                            log("Client #" + clientNumber + " (): connected");
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new PacketProcessorHandler(server, clientNumber)
                            );
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8189).sync();
            System.out.println("Server started");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
