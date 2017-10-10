package com.cncnc.gateserver.handler;

import com.cncnc.protobuf.protocol.Internal;
import com.cncnc.utils.ParseRegistryMap;
import com.cncnc.utils.Utils;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateToAuthConnectionHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(GateToAuthConnectionHandler.class);
    private static ChannelHandlerContext _gateToAuthConnection;

    public static ChannelHandlerContext getChannelHandlerContext(){
        return _gateToAuthConnection;
    }

    /**
     * hannel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        _gateToAuthConnection = ctx;
        logger.info("[Gate-Auth] connection is established");

        // 向Auth发送Greet
        sendGreetToAuth();
    }

    /**
     * 当从Channel中读取数据的时候调用
     * @param channelHandlerContext
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

    }


    private void sendGreetToAuth(){
        Internal.Greet.Builder greet = Internal.Greet.newBuilder();
        greet.setFrom(Internal.Greet.From.AuthServer);

        ByteBuf out = Utils.pack2Server(greet.build(), ParseRegistryMap.GREET, -1, Internal.Dest.AuthServer, "admin");
        getChannelHandlerContext().writeAndFlush(out);
        logger.info("Gate send Green to Auth.");
    }
}
