package serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoderHandler extends ByteToMessageDecoder {

    private SerializeType serializeType;
    private Class<?> genericType;

    public NettyDecoderHandler(SerializeType serializeType, Class<?> genericType) {
        this.genericType = genericType;
        this.serializeType = serializeType;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        //报头应该是4个Bytes,如果少于四个bytes的话直接返回
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
            throw new RuntimeException("数据长度小于0");
        }

        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        Object obj = SerializerFactory.deserialize(data, genericType, serializeType.getSerializeType());
        list.add(obj);

    }


}
