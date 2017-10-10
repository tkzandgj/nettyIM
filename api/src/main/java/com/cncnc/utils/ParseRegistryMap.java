package com.cncnc.utils;

import com.cncnc.analysis.ParseMap;
import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.login.Auth;
import com.cncnc.protobuf.protocol.Internal;

import java.io.IOException;

public class ParseRegistryMap {

    public static final int GTRANSFER = 900;
    public static final int GREET = 901;
    public static final int C_LOGIN = 1000;

    public static final int C_REGISTER = 1001;
    public static final int S_RESPONSE = 1002;
    public static final int C_PRIVATE_CHAT = 1003;
    public static final int S_PRIVATE_CHAT = 1004;


    public static void initRegistry() throws IOException {
        // 内部传输协议专用
        ParseMap.register(900, Internal.GTransfer::parseFrom, Internal.GTransfer.class);
        ParseMap.register(901, Internal.Greet::parseFrom, Internal.Greet.class);


        ParseMap.register(1000, Auth.CLogin::parseFrom, Auth.CLogin.class);
        ParseMap.register(1001, Auth.CRegister::parseFrom, Auth.CRegister.class);
        ParseMap.register(1002, Auth.SResponse::parseFrom, Auth.SResponse.class);

        ParseMap.register(1003, Chat.CPrivateChat::parseFrom, Chat.CPrivateChat.class);
        ParseMap.register(1004, Chat.SPrivateChat::parseFrom, Chat.SPrivateChat.class);
    }
}
