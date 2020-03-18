package protocol.dubbo;

import com.damon.RpcRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ChannelHandlerContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        System.out.println(channelHandlerContext.channel().remoteAddress() + "->server:" + rpcRequest.toString());
        //服务端调用反射得来的函数并且返回
        InvokeTask task = new InvokeTask(rpcRequest, channelHandlerContext);
        nettyServer.submit(task);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.context = ctx;
    }
}
