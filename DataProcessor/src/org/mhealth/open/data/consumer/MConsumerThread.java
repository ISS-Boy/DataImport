package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.queue.MDelayQueue;
import org.mhealth.open.data.reader.MRecord;

import java.util.concurrent.BlockingQueue;

/**
 * for DataImport
 *
 * @author just on 2017/10/9.
 */
public class MConsumerThread implements Runnable {
    private Logger logger;// = Logger.getLogger(MConsumerThread.class);
    private MDelayQueue measureQueue;
    private MProducer producer;

    public MConsumerThread(BlockingQueue measureQueue, MProducer producer) {
        this.measureQueue = (MDelayQueue) measureQueue;
        this.producer = producer;
    }

    public MConsumerThread(BlockingQueue measureQueue,Logger logger) {
        this.measureQueue = (MDelayQueue) measureQueue;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            int poisonCount = 0;
            while (true) {
                MRecord record = (MRecord) measureQueue.take();
                if (record.isPoisonPill())
                    poisonCount++;


                // producer.produce2Dest(record);
                logger.info("consume: " + record + ", queueSize_now: " + measureQueue.size() + ", recordNum: " + MConsumer.written.incrementAndGet());
                // 如果毒丸吃够了就跳出
                if (poisonCount >= ConfigurationSetting.READER_COUNT.get())
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
