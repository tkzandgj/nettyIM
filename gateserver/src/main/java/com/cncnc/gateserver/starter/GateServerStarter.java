package com.cncnc.gateserver.starter;

import com.cncnc.gateserver.GateServer;
import com.cncnc.gateserver.GateToAuthConnection;
import com.cncnc.gateserver.GateToLogicConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateServerStarter {

    private Logger logger = LoggerFactory.getLogger(GateServerStarter.class);

    private static final String GATE_TO_AUTH_HOST = "127.0.0.1";
    private static final Integer GATE_TO_AUTH_PORT = 8080;

    private static final String GATE_TO_LOGIC_HOST = "127.0.0.1";
    private static final Integer GATE_TO_LOGIC_PORT = 7070;

    private static final Integer GATE_PORT = 9090;

    public static void main(String[] args){

        new Thread(() -> GateServer.startGateServer(GATE_PORT)).start();

        //new Thread(() -> GateToAuthConnection.startGateToAuthConnection(GATE_TO_AUTH_HOST, GATE_TO_AUTH_PORT)).start();

        // 先去测试能不能连接到业务逻辑层
        new Thread(() -> GateToLogicConnection.startGateToLogicConnection(GATE_TO_LOGIC_HOST, GATE_TO_LOGIC_PORT)).start();

    }
}
