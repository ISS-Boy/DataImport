package org.mhealth.open.data.queue;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dujijun on 2017/10/13.
 */
public class MDelayQueue<E extends java.util.concurrent.Delayed> extends DelayQueue<E>{
    @Deprecated
    private AtomicInteger poisonNumNow = new AtomicInteger(0);
    private AtomicInteger mesCountNow = new AtomicInteger(0);

    @Deprecated
    public boolean enoughPoisonPill() {
        return poisonNumNow.get() >= ConfigurationSetting.READER_COUNT.get();
    }

    @Deprecated
    public void increasePoisonCount(){
        this.poisonNumNow.getAndIncrement();
    }

    public int getAndIncrement(){
        return mesCountNow.getAndIncrement();
    }
}
