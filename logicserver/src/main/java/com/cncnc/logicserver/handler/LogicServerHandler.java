package com.cncnc.logicserver.handler;

import com.cncnc.analysis.ParseMap;
import com.cncnc.logicserver.HandlerManager;
import com.cncnc.logicserver.IMHandler;
import com.cncnc.logicserver.Worker;
import com.cncnc.protobuf.protocol.Internal;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(LogicServerHandler.class);
    private static ChannelHandlerContext _gateToLogicConnection;
    private static ChannelHandlerContext _authToLogicConnection;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        Internal.GTransfer gTransfer = (Internal.GTransfer)message;
        int ptoNum = gTransfer.getPtoNum();
        Message msg = ParseMap.getMessage(ptoNum, gTransfer.getMsg().toByteArray());

        IMHandler handler;
        if (msg instanceof Internal.Greet){   // 表示服务要建立连接
            handler = HandlerManager.getHandler(ptoNum, gTransfer.getUserId(), gTransfer.getNetId(), msg, channelHandlerContext);
        } else {                              // 表示服务已经建立连接，准备要发送消息
            handler = HandlerManager.getHandler(ptoNum, gTransfer.getUserId(), gTransfer.getNetId(), msg, getGateToLogicConnection());
        }

        Worker.dispatch(gTransfer.getUserId(), handler);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    public static void setGateToLogicConnection(ChannelHandlerContext ctx){
        _gateToLogicConnection = ctx;
    }

    public static ChannelHandlerContext getGateToLogicConnection(){
        if (_gateToLogicConnection != null){
            return _gateToLogicConnection;
        } else {
            return null;
        }
    }

    public static void setAuthToLogicConnection(ChannelHandlerContext ctx){
        _authToLogicConnection = ctx;
    }

    public static ChannelHandlerContext getAuthToLogicConnection(){
        if (_authToLogicConnection != null){
            return _authToLogicConnection;
        } else {
            return null;
        }
    }
}
