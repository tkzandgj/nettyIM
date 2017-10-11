package com.cncnc.authserver.handler;

import com.cncnc.analysis.ParseMap;
import com.cncnc.authserver.IMHandler;
import com.cncnc.protobuf.protocol.Internal;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServerHandler extends SimpleChannelInboundHandler<Message>{

    private static final Logger logger = LoggerFactory.getLogger(AuthServerHandler.class);
    private static ChannelHandlerContext _gateToAuthConnection;

    public static void setGateToAuthConnection(ChannelHandlerContext ctx){
        _gateToAuthConnection = ctx;
    }

    public static ChannelHandlerContext getGateToAuthConnection(){
        if (_gateToAuthConnection != null){
            return _gateToAuthConnection;
        } else {
            return null;
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        // 获取服务间的中转协议
        Internal.GTransfer gTransfer = (Internal.GTransfer)message;
        int ptoNum = gTransfer.getPtoNum();
        Message msg = ParseMap.getMessage(ptoNum, gTransfer.getMsg().toByteArray());

        IMHandler handler;
        if (msg instanceof Internal.Greet){

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
