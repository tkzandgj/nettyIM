package com.cncnc.authserver;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public abstract class IMHandler {

    protected final String _userId;
    protected final long _netId;
    protected final Message _msg;
    protected ChannelHandlerContext _ctx;

    public IMHandler(String _userId, long _netId, Message _msg, ChannelHandlerContext _ctx) {
        this._userId = _userId;
        this._netId = _netId;
        this._msg = _msg;
        this._ctx = _ctx;
    }

    protected abstract void execute(Worker worker) throws IOException;
}
