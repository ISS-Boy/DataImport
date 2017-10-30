package org.mhealth.open.data.monitor;

import org.mhealth.open.data.reader.SFileReader;

import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/23.
 */
public class SMonitorThread extends MonitorThread {

    private final SFileReader reader;

    SMonitorThread(SFileReader reader, CountDownLatch startupLatch){
        super(startupLatch);
        this.reader = reader;
    }

    @Override
    protected void monitor() throws InterruptedException {
        // 在这里编写监控逻辑

    }
}
