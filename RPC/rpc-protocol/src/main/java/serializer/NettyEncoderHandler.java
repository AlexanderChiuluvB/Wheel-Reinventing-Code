package serializer;

import com.damon.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoderHandler extends MessageToByteEncoder {

    private SerializeType serializeType;

    public NettyEncoderHandler(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        //对象转化为字节数组
        byte[] data = SerializerFactory.serialize(o, serializeType.getSerializeType());
        //把字节数组作为消息头写入,解决粘包问题
        byteBuf.writeInt(data.length);
        //写入序列化后得到的字节数组
        byteBuf.writeBytes(data);

    }
}
