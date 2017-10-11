package com.cncnc.logicserver;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public abstract class IMHandler {

    protected final String _userId;
    protected final long _netId;
    protected final Message _msg;
    protected ChannelHandlerContext _ctx;

    protected IMHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx){
        this._userId = userId;
        this._netId = netId;
        this._msg = msg;
        this._ctx = ctx;
    }

    protected abstract void execute(Worker worker) throws IOException;
}
