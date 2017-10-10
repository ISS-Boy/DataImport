package org.mhealth.open.data.monitor;

import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.reader.MThreadController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MMonitor extends MThreadController {

    public void startMonitor(MFileReader reader, Map<String, BlockingQueue> queueMaps){
        CountDownLatch startupLatch = new CountDownLatch(1);

        setStartupLatch(startupLatch);
        Thread monitor = new Thread(new MMonitorThread(reader, startupLatch, queueMaps));
        monitor.start();
    }
}
