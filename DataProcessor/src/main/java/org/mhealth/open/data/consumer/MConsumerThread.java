package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.reader.MRecord;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * for DataImport
 *
 * @author just on 2017/10/9.
 */
public class MConsumerThread implements Runnable {
    private Logger logger;
    private DelayQueue measureQueue;
    private MProducer producer;

    public MConsumerThread(BlockingQueue measureQueue, MProducer producer,Logger logger) {
        this.logger = logger;
        this.measureQueue = (DelayQueue) measureQueue;
        this.producer = producer;
    }


    @Override
    public void run() {
        try {
            while (true) {
                MRecord record = (MRecord) measureQueue.take();
//                if (record.isPoisonPill()){
//                    producer.close();
//                    break;
//                }
                producer.produce2Dest(record);
                logger.info("consume: " + record + ", queueSize_now: " + measureQueue.size() + ", recordNum: " + MConsumer.written.incrementAndGet());

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
