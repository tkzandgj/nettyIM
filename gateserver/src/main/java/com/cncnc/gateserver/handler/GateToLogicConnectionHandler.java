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

public class GateToLogicConnectionHandler extends SimpleChannelInboundHandler<Message>{

    private static final Logger logger = LoggerFactory.getLogger(GateToLogicConnectionHandler.class);
    private static ChannelHandlerContext _gateToLogicConnection;

    public static ChannelHandlerContext getGateToLogicConnection(){
        return _gateToLogicConnection;
    }

    /**
     * Channel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
     * gateServer 服务连接到 logicServer服务的时候会调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        _gateToLogicConnection = ctx;
        logger.info("[Gate-Logic] connection is established");

        // 向Logic发送Greet
        sendGreetToLogic();
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


    private void sendGreetToLogic(){
        Internal.Greet.Builder greet = Internal.Greet.newBuilder();
        greet.setFrom(Internal.Greet.From.GateServer);

        ByteBuf out = Utils.pack2Server(greet.build(), ParseRegistryMap.GREET, -1, Internal.Dest.LogicServer, "admin");

        getGateToLogicConnection().writeAndFlush(out);
        logger.info("Gate send Green to Logic.");
    }
}
