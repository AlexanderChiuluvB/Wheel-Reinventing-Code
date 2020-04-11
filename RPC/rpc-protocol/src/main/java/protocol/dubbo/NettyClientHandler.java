package protocol.dubbo;

import com.damon.MessageCallBack;
import com.damon.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.dubbo.channelPool.ResponseHolder;

import java.util.Date;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止时间是：" + new Date());
        System.out.println("HeartBeatClientHandler channelInactive");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("激活时间是：" + ctx.channel().id());
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {

        String responseId = rpcResponse.getResponseId();
        MessageCallBack callBack = ResponseHolder.getInstance().callBackConcurrentHashMap.get(responseId);
        if (callBack != null) {
            ResponseHolder.getInstance().callBackConcurrentHashMap.remove(responseId);
            callBack.over(rpcResponse);
        }
    }
}
