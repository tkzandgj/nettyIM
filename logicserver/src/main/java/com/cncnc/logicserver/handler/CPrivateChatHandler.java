package com.cncnc.logicserver.handler;

import com.cncnc.logicserver.IMHandler;
import com.cncnc.logicserver.Worker;
import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.login.Auth;
import com.cncnc.protobuf.protocol.Internal;
import com.cncnc.utils.ParseRegistryMap;
import com.cncnc.utils.Utils;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CPrivateChatHandler extends IMHandler{
    private static final Logger logger = LoggerFactory.getLogger(CPrivateChatHandler.class);

    protected CPrivateChatHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
    }

    @Override
    protected void execute(Worker worker) throws IOException {
        Chat.CPrivateChat msg = (Chat.CPrivateChat)_msg;
        ByteBuf byteBuf = null;

        // 转发给auth
        byteBuf = Utils.pack2Server(_msg, ParseRegistryMap.C_PRIVATE_CHAT, Internal.Dest.AuthServer, msg.getDest());
        LogicServerHandler.getAuthToLogicConnection().writeAndFlush(byteBuf);


        Auth.SResponse.Builder response = Auth.SResponse.newBuilder();
        response.setCode(300);
        response.setDesc("Server received message success!!!");
        byteBuf = Utils.pack2Client(response.build());
        _ctx.writeAndFlush(byteBuf);
    }
}
