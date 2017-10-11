package com.cncnc.logicserver;

import com.cncnc.analysis.ParseMap;
import com.cncnc.logicserver.handler.CPrivateChatHandler;
import com.cncnc.logicserver.handler.GreetHandler;
import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.protocol.Internal;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HandlerManager {

    private static final Logger logger = LoggerFactory.getLogger(HandlerManager.class);

    private static final Map<Integer, Constructor<? extends IMHandler>> _handler = new HashMap<>();

    public static void register(Class<? extends Message> msg, Class<? extends IMHandler> handler){
        int num = ParseMap.getPtoNum(msg);

        try {
            Constructor<? extends IMHandler> constructor = handler.getConstructor(String.class, long.class,
                    Message.class, ChannelHandlerContext.class);
            _handler.put(num, constructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对应的Handler,通过反射去调用
     * @param msgNum
     * @param userId
     * @param netId
     * @param msg
     * @param ctx
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static IMHandler getHandler(int msgNum, String userId, long netId,
                                       Message msg, ChannelHandlerContext ctx)
            throws IllegalAccessException, InvocationTargetException, InstantiationException{
        Constructor<? extends IMHandler> constructor = _handler.get(msgNum);
        if (constructor == null){
            logger.error("handler not exist, Message Number: {}", msgNum);
            return null;
        }
        return constructor.newInstance(userId, netId, msg, ctx);
    }

    /**
     * 初始化所有的Handler
     */
    public static void initHandlers(){
        HandlerManager.register(Internal.Greet.class, GreetHandler.class);
        HandlerManager.register(Chat.CPrivateChat.class, CPrivateChatHandler.class);
    }
}
