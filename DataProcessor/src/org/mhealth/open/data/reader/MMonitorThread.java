package org.mhealth.open.data.reader;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MMonitorThread extends AbstractMThread {

    private final Map<String, Queue> queueMaps;

    MMonitorThread(CountDownLatch startupLatch, Map<String, Queue> queueMaps){
        super(startupLatch);
        this.queueMaps = queueMaps;
    }

    @Override
    public void run() {
        startupComplete();

        // 监控主逻辑
        monitor();

        shutdownComplete();
    }

    // 这里开始监控主逻辑
    private void monitor() {

    }
}
