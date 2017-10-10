package com.cncnc.code;

import com.cncnc.analysis.ParseMap;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> list) throws Exception {
        in.markReaderIndex();

        if (in.readableBytes() < 4){
            logger.info("readableBytes length less than 4 bytes, ignored");
            in.resetReaderIndex();
            return;
        }

        int length = in.readInt();

        if (length < 0){
            ctx.close();
            logger.info("message length less than 0, channel closed");
            return;
        }

        if (length > in.readableBytes() - 4){
            in.resetReaderIndex();
            return;
        }

        int ptoNum = in.readInt();

        ByteBuf byteBuf = Unpooled.buffer(length);

        in.readBytes(byteBuf);

        try {
            byte[] body = byteBuf.array();

            Message msg = ParseMap.getMessage(ptoNum, body);
            list.add(msg);
            logger.info("GateServer Received Message: content length {}, ptoNum: {}", length, ptoNum);
        } catch (Exception e) {
            logger.error(ctx.channel().remoteAddress() + ", decode failed.", e);
        }
    }
}
