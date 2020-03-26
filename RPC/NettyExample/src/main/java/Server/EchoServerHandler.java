package Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable // Indicates that the hanlder can be safely shared by multiple channels
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * Called for each incoming message
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("发送方发来的消息是: " + in.toString(CharsetUtil.UTF_8));
        // Write the message to the sender without flushing the outbound messages
        ctx.write(in);
    }

    /**
     * Notify the handler that the last call made to channelRead() was the last msg
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);//Flushes pending msg to remote peer and close the channel

    }

    /**
     *
     * @param ctx
     * @param throwable
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        throwable.printStackTrace();
        ctx.close();
    }


}
