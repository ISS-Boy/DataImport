package org.mhealth.open.data.monitor;

import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.configuration.MeasureConfiguration;
import org.mhealth.open.data.reader.AbstractMThread;
import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.reader.MRecord;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/5.
 */
public abstract class MonitorThread extends AbstractMThread {

    MonitorThread(CountDownLatch startupLatch){
        super(startupLatch);
    }

    @Override
    public void run() {
        startupComplete();

        try {
            monitor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // 监控主逻辑
    protected abstract void monitor() throws InterruptedException;
}
