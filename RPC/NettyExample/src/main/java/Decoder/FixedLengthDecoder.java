package Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthDecoder extends ByteToMessageDecoder {

    private final int FrameLength;

    public FixedLengthDecoder(int frameLength) {
        FrameLength = frameLength;
    }


    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        while (in.readableBytes() >= this.FrameLength) {
            ByteBuf byteBuf = in.readBytes(this.FrameLength);
            out.add(byteBuf);
        }

    }
}
