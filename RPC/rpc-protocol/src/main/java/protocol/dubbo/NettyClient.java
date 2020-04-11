package protocol.dubbo;

import com.damon.Configuration;
import com.damon.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import serializer.NettyDecoderHandler;
import serializer.NettyEncoderHandler;
import serializer.SerializeType;

public class NettyClient {

    private static NettyClient INSTANCE = null;

    private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());

    private NettyClient() {
    }

    public static NettyClient getInstance() {
        synchronized (NettyClient.class) {
            while (INSTANCE == null) {
                synchronized (NettyClient.class) {
                    INSTANCE = new NettyClient();
                    return INSTANCE;
                }
            }
        }
        return null;
    }

    private final static Integer parallelism = Runtime.getRuntime().availableProcessors() * 2;

    public void start(String host, Integer port) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup(parallelism);

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyEncoderHandler(serializeType));
                            pipeline.addLast(new NettyDecoderHandler(serializeType, RpcResponse.class));
                            pipeline.addLast("handler", new NettyClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
