package com.cncnc.logicserver.starter;

import com.cncnc.logicserver.LogicServer;
import com.cncnc.logicserver.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicServerStarter {

    private static final Logger logger = LoggerFactory.getLogger(LogicServerStarter.class);

    public static int workNum = 1;

    private static final Integer PORT = 7070;

    public static void main(String[] args){
        //启动worker线程
        Worker.startWorker(workNum);

        // 重新开启一个线程去执行LogicServer
        // 启动一个LogicServer等待Gateserver的连接
        new Thread(() -> LogicServer.startLogicServer(PORT)).start();
    }
}
