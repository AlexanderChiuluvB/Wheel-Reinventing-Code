package protocol.dubbo;

import com.damon.MessageCallBack;
import com.damon.RpcRequest;
import com.damon.URL;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import protocol.Protocol;
import protocol.dubbo.channelPool.NettyChannelPoolFactory;
import protocol.dubbo.channelPool.ResponseHolder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class dubboProtocol implements Protocol {

    @Override
    public void start(URL url) {
        NettyServer server = NettyServer.getInstance();
        server.start(url.getHost(), url.getPort());
    }

    /**
     * 1.从阻塞队列拿出一个Channel处理事件,如果事件为空重新注册一个
     * 2.把调用invocation写入Channel,发起异步调用
     * 3.注册一个CallBack,使用回调得到结果
     *
     * @param url
     * @param invocation
     * @return
     */
    @Override
    public Object send(URL url, RpcRequest invocation) {
        // 每个URL负责一个Netty的阻塞队列
        ArrayBlockingQueue<Channel> queue = NettyChannelPoolFactory.getInstance().acquire(url);
        Channel channel = null;
        try {
            channel = queue.poll(invocation.getTimeout(), TimeUnit.MILLISECONDS);
            //如果channel不可用再从阻塞队列拉一个一个新的channel
            if (channel == null || !channel.isWritable() || !channel.isOpen() || !channel.isRegistered()) {
                channel = queue.poll(invocation.getTimeout(), TimeUnit.MILLISECONDS);
                //如果拉出来的还是空的话 就新注册一个
                if (channel == null) {
                    channel = NettyChannelPoolFactory
                            .getInstance()
                            .registerChannel(url);
                }
            }
            //本次调用信息写入Channel,发起异步调用
            ChannelFuture channelFuture = channel.writeAndFlush(invocation);
            channelFuture.syncUninterruptibly();
            MessageCallBack callBack = new MessageCallBack(invocation);
            ResponseHolder.getInstance().callBackConcurrentHashMap.put(invocation.getRequestId(), callBack);
            try {
                return callBack.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("release: " + channel.id());
            NettyChannelPoolFactory.getInstance().release(queue, channel, url);
        }
        return null;
    }
}
