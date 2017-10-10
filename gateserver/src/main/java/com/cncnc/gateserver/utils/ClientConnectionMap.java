package com.cncnc.gateserver.utils;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionMap {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionMap.class);

    // 保存一个gateway上的所有的客户端的连接
    public static ConcurrentHashMap<Long, ClientConnection> allClientConnectionMap = new ConcurrentHashMap<Long, ClientConnection>();
    // 一个用户Id(userId) 对应 一个NetId
    private static ConcurrentHashMap<String, Long> userId2NetIdMap = new ConcurrentHashMap<String, Long>();

    public static ClientConnection getClientConnection(ChannelHandlerContext ctx){
        Long netId = ctx.attr(ClientConnection.NETID).get();

        ClientConnection conn = allClientConnectionMap.get(netId);
        if (conn != null){
            return conn;
        } else {
            logger.error("ClientConenction not found in allClientMap, netId: {}", netId);
        }
        return null;
    }


    public static ClientConnection getClientConnection(Long netId){
        ClientConnection conn = allClientConnectionMap.get(netId);

        if (conn != null){
            return conn;
        } else {
            logger.error("ClientConenction not found in allClientMap, netId: {}", netId);
        }
        return null;
    }


    public static void addClientConnetion(ChannelHandlerContext ctx){

        ClientConnection conn = new ClientConnection(ctx);

        // 判断用户是否登录过
        if (ClientConnectionMap.allClientConnectionMap.putIfAbsent(conn.getNetId(), conn) != null){
            logger.error("Duplicated netid");
        }
    }


    public static void removeClientConnetion(ChannelHandlerContext ctx){

        ClientConnection conn = new ClientConnection(ctx);
        Long netId = conn.getNetId();
        String userId = conn.getUserId();

        if (ClientConnectionMap.allClientConnectionMap.remove(netId) != null){
            unRegisterUserId(userId);
        } else {
            logger.error("NetId: {} is not exist in allClientMap", netId);
        }
    }

    protected static void unRegisterUserId(String userId){
        if (ClientConnectionMap.userId2NetIdMap.remove(userId) == null){
            logger.error("UserId: {} is not exist in userid2netidMap", userId);
        }
    }


    public static void registerUserId(String userId, Long netId){
        if (userId2NetIdMap.putIfAbsent(userId, netId) == null){
            ClientConnection conn = ClientConnectionMap.getClientConnection(netId);
            if (conn != null){
                conn.setUserId(userId);
            } else {
                logger.error("ClientConnection is null");
                return;
            }
        } else {
            logger.error("userid: {} has registered in userid2netidMap", userId);
        }
    }

}
