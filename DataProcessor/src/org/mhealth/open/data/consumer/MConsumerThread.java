package org.mhealth.open.data.consumer;

import org.mhealth.open.data.queue.MDelayQueue;
import org.mhealth.open.data.reader.MRecord;

import java.util.concurrent.BlockingQueue;

/**
 * for DataImport
 *
 * @author just on 2017/10/9.
 */
public class MConsumerThread implements Runnable{
    private MDelayQueue measureQueue;
    private MProducer producer;

    public MConsumerThread(BlockingQueue measureQueue, MProducer producer) {
        this.measureQueue = (MDelayQueue) measureQueue;
        this.producer = producer;
    }


    @Override
    public void run() {
        try{
            while(true){
                MRecord record = (MRecord) measureQueue.take();
                if(record.isPoisonPill())
                    measureQueue.increasePoisonCount();

                // 如果毒丸吃够了就跳出
                if(measureQueue.enoughPoisonPill())
                    break;
                // producer.produce2Dest(record);
                System.out.println("消费了数据" + record + ", 现在是队列中有" + measureQueue.size() + "条数据, 现在是第" + measureQueue.getAndIncrement());

            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }


}
