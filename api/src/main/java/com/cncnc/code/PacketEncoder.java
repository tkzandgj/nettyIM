package com.cncnc.code;

import com.cncnc.analysis.ParseMap;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketEncoder extends MessageToByteEncoder<Message> {

    private static final Logger logger = LoggerFactory.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Message message, ByteBuf byteBuf) throws Exception {

        byte[] bytes = message.toByteArray();
        int ptoNum = ParseMap.msg2ptoNum.get(message);
        int length = bytes.length;


        ByteBuf buf = Unpooled.buffer(8 + length);
        buf.writeInt(length);
        buf.writeInt(ptoNum);
        buf.writeBytes(bytes);
        byteBuf.writeBytes(buf);

        logger.info(" GateServer Send Message, remoteAddress: {}, content length {}, ptoNum: {}",
                channelHandlerContext.channel().remoteAddress(), length, ptoNum);
    }
}
