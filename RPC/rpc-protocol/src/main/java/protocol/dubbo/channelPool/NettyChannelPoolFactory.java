package protocol.dubbo.channelPool;

import com.damon.Configuration;
import com.damon.RpcResponse;
import com.damon.ServiceProvider;
import com.damon.URL;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import protocol.dubbo.NettyClientHandler;
import serializer.NettyDecoderHandler;
import serializer.NettyEncoderHandler;
import serializer.SerializeType;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class NettyChannelPoolFactory {

    // 初始化Channel的阻塞队列长度
    private static final int ChannelConnectSize = 10;

    private static final Map<URL, ArrayBlockingQueue<Channel>> channelPoolMap
            = new ConcurrentHashMap<>();

    private NettyChannelPoolFactory() {
    }

    private static NettyChannelPoolFactory INSTANCE = new NettyChannelPoolFactory();

    public static NettyChannelPoolFactory getInstance() {
        return INSTANCE;
    }

    private List<ServiceProvider> serviceProviderList = new ArrayList<>();

    public void initNettyChannelPoolFactory(Map<String, List<ServiceProvider>> map) {

        Collection<List<ServiceProvider>> collectionServiceProviderList =
                map.values();
        for (List<ServiceProvider> serviceProvider : collectionServiceProviderList) {
            if (serviceProvider.isEmpty()) {
                continue;
            }
            serviceProviderList.addAll(serviceProvider);
        }

        Set<URL> set = new HashSet<>();
        for (ServiceProvider serviceProvider : serviceProviderList) {
            String serviceIp = serviceProvider.getIp();
            int servicePort = serviceProvider.getPort();
            URL url = new URL(serviceIp, servicePort);
            set.add(url);
        }

        //为每个url注册多个channel
        for (URL url : set) {
            //为每个ip端口建立多个Channel 并且放入阻塞队列
            int channelCount = 0;
            while (channelCount < ChannelConnectSize) {
                Channel channel = null;
                while (channel == null) {
                    channel = registerChannel(url);
                }
                channelCount++;

                ArrayBlockingQueue<Channel> queue = channelPoolMap.get(url);

                if (queue == null) {
                    queue = new ArrayBlockingQueue<Channel>(ChannelConnectSize);
                    channelPoolMap.put(url, queue);
                }
                queue.offer(channel);

            }
        }
    }

    public Channel registerChannel(URL url) {

        final SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup(10);

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
            ChannelFuture future = bootstrap.connect(url.getHost(), url.getPort());
            Channel channel = future.channel();
            final List<Boolean> isSuccess = new ArrayList<>(1);

            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //等待Netty服务端链路建立信号
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        isSuccess.add(true);
                    }else{
                        isSuccess.add(false);
                    }
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            if(isSuccess.get(0)){
                return channel;
            }
        } catch (InterruptedException e) {
            group.shutdownGracefully();
            e.printStackTrace();
        }
        return null;
    }


    public ArrayBlockingQueue<Channel> acquire(URL url) {
        System.out.println(channelPoolMap.toString());
        return channelPoolMap.get(url);
    }

    //回收使用完毕后的Channel
    public void release(ArrayBlockingQueue<Channel> queue, Channel channel, URL url) {
        if(queue == null) {
            return;
        }
        //检查channel的可用性，若不可用则重新注册放入阻塞队列中
        if(channel==null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            if(channel != null) {
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }
            Channel c = null;
            while(c == null){
                c = registerChannel(url);
            }
            queue.offer(c);
            return;
        }
        queue.offer(channel);
    }
}
