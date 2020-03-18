package protocol.dubbo;

import com.damon.Configuration;
import com.damon.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import serializer.NettyDecoderHandler;
import serializer.NettyEncoderHandler;
import serializer.SerializeType;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class nettyServer {

    private static volatile nettyServer INSTANCE = null;

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());

    private nettyServer() {
    }

    public static nettyServer getInstance() {
        if (INSTANCE == null) {
            synchronized (nettyServer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new nettyServer();
                    return INSTANCE;
                }
            }
        }
        return INSTANCE;
    }

    public static void submit(Runnable t) {
        executorService.submit(t);
    }

    public void start(String host, Integer port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline channelPipeline = socketChannel.pipeline();
                                channelPipeline.addLast(new NettyDecoderHandler(serializeType, RpcRequest.class));
                                channelPipeline.addLast(new NettyEncoderHandler(serializeType));
                                channelPipeline.addLast("handler", new Ne)


                        }
                    });
        }




    }


}
