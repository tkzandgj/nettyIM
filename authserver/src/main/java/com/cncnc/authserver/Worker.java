package com.cncnc.authserver;

import com.cncnc.authserver.starter.AuthServerStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Worker extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    public static Worker[] _workers;

    public volatile boolean _stop = false;
    private final BlockingQueue<IMHandler> _tasks = new LinkedBlockingDeque<>();

    public static void dispatch(String userId, IMHandler handler){
        int wordId = getWorkId(userId);
        if (handler == null){
            logger.error("handler is null");
            return;
        }

        _workers[wordId]._tasks.offer(handler);
    }


    @Override
    public void run() {
        while (!_stop){
            IMHandler handler = null;
            try {
                handler = _tasks.poll(1000, TimeUnit.MILLISECONDS);
                if (handler == null){
                    continue;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Caught Exception");
            }

            try {
                assert handler != null;
                handler.execute(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getWorkId(String str){
        return str.hashCode() % AuthServerStarter.workNum;
    }


    public static void startWorker(int workNum){
        _workers = new Worker[workNum];
        for (int i = 0; i < workNum; i++){
            _workers[i] = new Worker();
            _workers[i].start();
        }
    }


    public static void stopWorker(){
        for (int i = 0; i < AuthServerStarter.workNum; i++){
            _workers[i]._stop = true;
        }
    }
}
