package com.cncnc.logicserver;

import com.cncnc.logicserver.starter.LogicServerStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Worker extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    public static Worker[] _workers;

    public volatile boolean _stop = false;
    private final BlockingQueue<IMHandler> _tasks = new LinkedBlockingDeque<>();

    public static void dispatch(String userId, IMHandler handler){
        int workId = getWorkId(userId);
        if (handler == null){
            logger.error("handler is null!!!");
            return;
        }
        _workers[workId]._tasks.offer(handler);
    }


    @Override
    public void run() {
        while (!_stop){
            IMHandler handler = null;
            try {
                handler = _tasks.poll(1000, TimeUnit.MILLISECONDS);  // 获取任务，超时时间设置为1000ms
                if (handler == null){
                    continue;
                }
            } catch (Exception e){
                logger.error("Caught Exception!!");
            }

            try {
                assert handler != null;
                handler.execute(this);
            } catch (Exception e) {
                logger.error("Caught Exception!!");
            }
        }
    }


    public static int getWorkId(String str){
        return str.hashCode() % LogicServerStarter.workNum;
    }


    public static void startWorker(int workNum){
        _workers = new Worker[workNum];
        for (int i = 0; i < workNum; i++){
            _workers[i] = new Worker();
            _workers[i].start();
        }
    }

    public static void stopWorker(){
        for (int i = 0; i < LogicServerStarter.workNum; i++){
            _workers[i]._stop = true;
        }
    }
}
