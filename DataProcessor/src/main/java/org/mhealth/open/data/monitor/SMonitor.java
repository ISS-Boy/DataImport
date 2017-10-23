package org.mhealth.open.data.monitor;

import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.SFileReader;

import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/23.
 */
public class SMonitor extends Monitor {
    @Override
    public void startMonitor(MDataReader reader) {
        CountDownLatch startupLatch = new CountDownLatch(1);

        setStartupLatch(startupLatch);
        Thread monitor = new Thread(new SMonitorThread((SFileReader) reader, startupLatch));
        monitor.start();
    }
}
