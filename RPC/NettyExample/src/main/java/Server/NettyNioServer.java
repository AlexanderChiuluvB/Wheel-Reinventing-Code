package Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyNioServer {

    public static void Server(int port) throws Exception {
        final ByteBuf byteBuf = Unpooled.copiedBuffer("Set up Netty Nio Server",
                Charset.forName("UTF-8"));
        EventLoopGroup eventLoopGroup = new EpollEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup)
                    .channel(EpollServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) {
                                            ctx.writeAndFlush(byteBuf.duplicate())
                                                    //Once the message is written close the connection.
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    });
                        }
                    });
            ChannelFuture f = serverBootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{

        Server(Integer.parseInt(args[0]));
    }


}
