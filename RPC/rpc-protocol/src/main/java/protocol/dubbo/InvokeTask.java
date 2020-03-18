package protocol.dubbo;

import com.damon.RpcRequest;
import com.damon.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

public class InvokeTask implements Runnable {

    private RpcRequest invocation;
    private ChannelHandlerContext ctx;

    public InvokeTask(RpcRequest invocation, ChannelHandlerContext ctx) {
        super();
        this.invocation= invocation;
        this.ctx = ctx;
    }

    @Override
    public void run() {

        Class implClass = null;
        try {
            implClass = Class.forName(invocation.getImpl());
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Method method;
        Object result = null;
        try {
            method = implClass.getMethod(invocation.getMethodName(), invocation.getParamTypes());
            result = method.invoke(implClass.newInstance(), invocation.getParams());
        }catch (Exception e){
            e.printStackTrace();
        }
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(invocation.getRequestId());
        rpcResponse.setData(result);
        ctx.writeAndFlush(rpcResponse)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("RPC Server Send message-id respone:" + invocation.getRequestId());
                    }
                });
    }
}
