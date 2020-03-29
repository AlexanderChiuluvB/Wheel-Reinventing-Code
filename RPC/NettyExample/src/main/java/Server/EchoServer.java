package Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if(args.length!=1){
            System.err.println("" +
                    "Usage: " + EchoServer.class.getSimpleName() + " <port>");
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    private void start() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    // When a new connection is accepted, a new child channel will be created
                    // A new instance of echoServerHandler will be added to Channel's ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(echoServerHandler);
                        }});
            //Bind the server asynchronously
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
