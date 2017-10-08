package org.mhealth.open.data.reader;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MMonitor extends MThreadController{

    public void startMonitor(MFileReader reader, Map<String, Queue> queueMaps){
        CountDownLatch startupLatch = new CountDownLatch(1);

        setStartupLatch(startupLatch);
        Thread monitor = new Thread(new MMonitorThread(reader, startupLatch, queueMaps));
        monitor.start();
    }
}
