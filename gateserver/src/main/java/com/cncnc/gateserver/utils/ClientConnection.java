package com.cncnc.gateserver.utils;

import com.cncnc.utils.ThreeDES;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class ClientConnection {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private String _userId;
    private Long _netId;
    private ChannelHandlerContext _ctx;

    private static final AtomicLong netIdGenerator = new AtomicLong(1);

    public static AttributeKey<Long> NETID = AttributeKey.valueOf("netid");
    public static AttributeKey<ThreeDES> ENCRYPT = AttributeKey.valueOf("encrypt");

    public ClientConnection(ChannelHandlerContext ctx){
        _netId = netIdGenerator.incrementAndGet();
        _ctx = ctx;

        // 每一个ChannelHandlerContext上如果有AttributeMap都是绑定上下文的，
        // 也就是说如果A的ChannelHandlerContext中的AttributeMap,B的ChannelHandlerContext是无法读取到的
        // 但是Channel上的AttributeMap就是大家共享的，每一个ChannelHandler都能够获取到
        _ctx.attr(ClientConnection.NETID).set(_netId);
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String _userId) {
        this._userId = _userId;
    }

    public Long getNetId() {
        return _netId;
    }

    public void setNetId(Long _netId) {
        this._netId = _netId;
    }

    public ChannelHandlerContext getCtx() {
        return _ctx;
    }

    public void setCtx(ChannelHandlerContext _ctx) {
        this._ctx = _ctx;
    }
}
