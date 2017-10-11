package com.cncnc.authserver.handler;

import com.cncnc.authserver.IMHandler;
import com.cncnc.authserver.Worker;
import com.cncnc.protobuf.login.Auth;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CLoginHandler extends IMHandler {

    private static final Logger logger = LoggerFactory.getLogger(CLoginHandler.class);

    public CLoginHandler(String _userId, long _netId, Message _msg, ChannelHandlerContext _ctx) {
        super(_userId, _netId, _msg, _ctx);
    }

    @Override
    protected void execute(Worker worker) throws IOException {
        Auth.CLogin msg = (Auth.CLogin)_msg;

    }
}
