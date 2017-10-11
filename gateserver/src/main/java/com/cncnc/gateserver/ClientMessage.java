package com.cncnc.gateserver;

import com.cncnc.analysis.ParseMap;
import com.cncnc.gateserver.handler.GateToAuthConnectionHandler;
import com.cncnc.gateserver.handler.GateToLogicConnectionHandler;
import com.cncnc.gateserver.utils.ClientConnection;
import com.cncnc.gateserver.utils.ClientConnectionMap;
import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.login.Auth;
import com.cncnc.protobuf.protocol.Internal;
import com.cncnc.utils.Utils;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class ClientMessage {
    private static final Logger logger = LoggerFactory.getLogger(ClientMessage.class);

    public static HashMap<Integer, Transfer> transferHashMap = new HashMap<Integer, Transfer>();
    public static HashMap<Class<?>, Integer> msgToPtoNum = new HashMap<Class<?>, Integer>();


    // "函数式接口"是指仅仅只包含一个抽象方法的接口
    @FunctionalInterface
    public interface Transfer{
        void process(Message message, ClientConnection conn) throws IOException;
    }

    public static void registerTransferHandler(Integer ptoNum, Transfer transfer, Class<?> cla){

        if (transferHashMap.get(ptoNum) == null){
            transferHashMap.put(ptoNum, transfer);
        } else {
            logger.error("pto has been registered in transeerHandlerMap, ptoNum: {}", ptoNum);
            return;
        }

        if (msgToPtoNum.get(cla) == null){
            msgToPtoNum.put(cla, ptoNum);
        } else {
            logger.error("pto has been registered in msg2ptoNum, ptoNum: {}", ptoNum);
            return;
        }
    }


    public static void processTransferHandler(Message message, ClientConnection conn)
            throws IOException{
        logger.info("MessageName {}", message.getClass());

        int ptoNum = msgToPtoNum.get(message.getClass());
        Transfer transferHandler = transferHashMap.get(ptoNum);

        if (transferHandler != null){
            transferHandler.process(message, conn);
        }
    }


    public static void transferToLogic(Message message, ClientConnection conn){
        ByteBuf byteBuf = null;
        if (conn.getUserId() == null){
            logger.error("user not login!");
            return;
        }

        if (message instanceof Chat.CPrivateChat){
            byteBuf = Utils.pack2Server(message, ParseMap.getPtoNum(message), conn.getNetId(), Internal.Dest.LogicServer, conn.getUserId());
        }

        GateToLogicConnectionHandler.getGateToLogicConnection().writeAndFlush(byteBuf);
    }


    public static void transferToAuth(Message message, ClientConnection conn){
        ByteBuf byteBuf = null;

        if (message instanceof Auth.CLogin){
            String userId = ((Auth.CLogin)message).getUserid();
            byteBuf = Utils.pack2Server(message, ParseMap.getPtoNum(message), conn.getNetId(), Internal.Dest.AuthServer, userId);
            ClientConnectionMap.registerUserId(userId, conn.getNetId());
        } else if (message instanceof Auth.CRegister){
            byteBuf = Utils.pack2Server(message, ParseMap.getPtoNum(message), conn.getNetId(), Internal.Dest.AuthServer, ((Auth.CRegister)message).getUserid());
        }

        GateToAuthConnectionHandler.getChannelHandlerContext().writeAndFlush(byteBuf);
    }
}
