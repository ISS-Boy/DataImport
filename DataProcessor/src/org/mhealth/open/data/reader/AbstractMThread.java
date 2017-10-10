package org.mhealth.open.data.reader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dujijun on 2017/10/5.
 */
public abstract class AbstractMThread implements Runnable {

    //开启线程闭锁
    private final CountDownLatch startupLatch;
    private final CountDownLatch completeLatch;
    private final CountDownLatch shutdownLatch;

    public AbstractMThread(CountDownLatch startupLatch, CountDownLatch completeLatch, CountDownLatch shutdownLatch) {
        this.startupLatch = startupLatch;
        this.completeLatch = completeLatch;
        this.shutdownLatch = shutdownLatch;
    }

    AbstractMThread(){
        this(new CountDownLatch(0), new CountDownLatch(0), new CountDownLatch(0));
    }

    public AbstractMThread(CountDownLatch startupLatch){
        this(startupLatch, new CountDownLatch(0), new CountDownLatch(0));
    }

    AbstractMThread(CountDownLatch startupLatch, CountDownLatch completeLatch){
        this(startupLatch, completeLatch, new CountDownLatch(0));
    }

    //完全启动线程
    public void startupComplete() {
        startupLatch.countDown();
    }

    // 准备完全关闭线程
    public void shutdownComplete() {
        shutdownLatch.countDown();
    }

    // 准备一齐完成某些动作
    public void workComplete(){
        completeLatch.countDown();
    }

}
