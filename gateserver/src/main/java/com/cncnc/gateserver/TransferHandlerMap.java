package com.cncnc.gateserver;

import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.login.Auth;

import java.io.IOException;

public class TransferHandlerMap {

    public static void initRegistry() throws IOException {
        ClientMessage.registerTransferHandler(1000, ClientMessage::transferToAuth, Auth.CLogin.class);
        ClientMessage.registerTransferHandler(1001, ClientMessage::transferToAuth, Auth.CRegister.class);
        ClientMessage.registerTransferHandler(1003, ClientMessage::transferToLogic, Chat.CPrivateChat.class);
    }
}
