package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.reader.MRecord;
import org.mhealth.open.data.record.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SConsumerThread implements Runnable {
    private Logger logger;// = Logger.getLogger(MConsumerThread.class);
    private DelayQueue measureQueue;
    private SProducer producer;
    public SConsumerThread(BlockingQueue measureQueue, SProducer producer, Logger logger) {
        this.logger = logger;
        this.measureQueue = (DelayQueue) measureQueue;
        this.producer = producer;
    }

    public SConsumerThread(BlockingQueue measureQueue,Logger logger) {
        this.measureQueue = (DelayQueue) measureQueue;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {

            int size = measureQueue.size();
            for(int n=0;n<size;n++){
                SRecord record = (SRecord) measureQueue.take();
                if(record instanceof Allergies){
                    Allergies allergies = (Allergies)record;
                    producer.produce2Dest(allergies);
                }

                else if(record instanceof Observations){
                    Observations observations = (Observations) record;
                    producer.produce2Dest(observations);
                }
                else if(record instanceof Patients){
                    Patients patients = (Patients) record;
                    producer.produce2Dest(patients);
                }
                else
                    producer.produce2Dest(record);

                logger.info("consume: " + record.getDescription() + ", queueSize_now: " + measureQueue.size() + ", recordNum: " + SConsumer.written.incrementAndGet());
            }

//            int size = measureQueue.size();
//            for(int n=0;n<size;n++){
//                SRecord record = (SRecord) measureQueue.take();
//                producer.produce2Dest(record);
//                logger.info("consume: " + record + ", queueSize_now: " + measureQueue.size() + ", recordNum: " + SConsumer.written.incrementAndGet());
//            }
//            producer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

