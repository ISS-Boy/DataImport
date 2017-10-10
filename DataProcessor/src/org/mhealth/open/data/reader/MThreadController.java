package org.mhealth.open.data.reader;

import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/6.
 */
public class MThreadController {

    private CountDownLatch startupLatch;
    private CountDownLatch shutdownLatch;
    private CountDownLatch completeLatch;

    public MThreadController() {
        startupLatch = new CountDownLatch(0);
        shutdownLatch = new CountDownLatch(0);
        completeLatch = new CountDownLatch(0);
    }

    protected void setStartupLatch(CountDownLatch startupLatch) {
        this.startupLatch = startupLatch;
    }

    protected void setShutdownLatch(CountDownLatch shutdownLatch) {
        this.shutdownLatch = shutdownLatch;
    }

    protected void setCompleteLatch(CountDownLatch completeLatch) {
        this.completeLatch = completeLatch;
    }

    public void waitForThreadsStartup() throws InterruptedException {
        startupLatch.await();
    }

    public void waitForThreadsShutdown() throws InterruptedException {
        shutdownLatch.await();
    }

    public void waitForThreadsWorkDown() throws InterruptedException {
        completeLatch.await();
    }
}
