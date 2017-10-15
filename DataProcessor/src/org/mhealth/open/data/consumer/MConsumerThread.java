package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.queue.MDelayQueue;
import org.mhealth.open.data.reader.MRecord;

import java.util.concurrent.BlockingQueue;

/**
 * for DataImport
 *
 * @author just on 2017/10/9.
 */
public class MConsumerThread implements Runnable{
//    private static Logger loggerBP = LoggerFactory.getLogger("bloodPressure");
//    private static Logger loggerHR = LoggerFactory.getLogger("heart");
    private static Logger logger = Logger.getLogger(MConsumerThread.class);

    private MDelayQueue measureQueue;
    private MProducer producer;
    private String measure;


    public MConsumerThread(BlockingQueue measureQueue, MProducer producer) {
        this.measureQueue = (MDelayQueue) measureQueue;
//        this.producer = producer;
    }
    public MConsumerThread(BlockingQueue measureQueue){
        this.measureQueue = (MDelayQueue) measureQueue;
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
                logger.info(record);
                MConsumer.written.incrementAndGet();
//                producer.produce2Dest(record);

            }
        }catch (InterruptedException e){
            e.printStackTrace();

        }

    }


}
