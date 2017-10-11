package com.cncnc.logicserver.handler;

import com.cncnc.logicserver.IMHandler;
import com.cncnc.logicserver.Worker;
import com.cncnc.protobuf.protocol.Internal;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 服务间建立连接时发送协议的Handler
 */
public class GreetHandler extends IMHandler {

    private static final Logger logger = LoggerFactory.getLogger(GreetHandler.class);

    public GreetHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
    }

    @Override
    protected void execute(Worker worker) throws IOException {
        Internal.Greet msg = (Internal.Greet)_msg;
        Internal.Greet.From from = msg.getFrom();

        if (from == Internal.Greet.From.AuthServer){  // auth 到 logic的连接已经建立
            LogicServerHandler.setAuthToLogicConnection(_ctx);
            logger.info("[Auth-Logic] connection is established");
        } else if (from == Internal.Greet.From.GateServer) {  // gate 到 logic的连接已经建立
            LogicServerHandler.setGateToLogicConnection(_ctx);
            logger.info("[Gate-Logic] connection is established");
        }
    }
}
