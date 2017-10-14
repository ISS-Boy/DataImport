package org.mhealth.open.data.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dujijun on 2017/10/13.
 */
public class MDelayQueue<E extends java.util.concurrent.Delayed> extends DelayQueue<E>{
    private int totalPoisonCount;
    private AtomicInteger poisonNumNow;

    public int getTotalPoisonCount() {
        return totalPoisonCount;
    }

    public void setTotalPoisonCount(int totalPoisonCount) {
        this.totalPoisonCount = totalPoisonCount;
    }

    public boolean enoughPoisonPill() {
        return poisonNumNow.get() == totalPoisonCount;
    }

    public void increasePoisonCount(){
        this.poisonNumNow.getAndIncrement();
    }
}
