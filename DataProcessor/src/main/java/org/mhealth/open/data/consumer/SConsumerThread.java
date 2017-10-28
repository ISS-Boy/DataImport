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
    private String Symeasurename;
    public SConsumerThread(String Symeasurename,BlockingQueue measureQueue, SProducer producer, Logger logger) {
        this.Symeasurename = Symeasurename;
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
            while (true) {
                switch (Symeasurename) {
                    case "allergies":
                        producer.produce2Dest(Symeasurename,(Allergies) measureQueue.take());
                        break;
                    case "careplans":
                        producer.produce2Dest(Symeasurename,(CarePlans) measureQueue.take());
                        break;
                    case "conditions":
                        producer.produce2Dest(Symeasurename,(Conditions) measureQueue.take());
                        break;
                    case "encounters":
                        producer.produce2Dest(Symeasurename,(Encounters) measureQueue.take());
                        break;
                    case "immunizations":
                        producer.produce2Dest(Symeasurename,(Immunizations) measureQueue.take());
                        break;
                    case "medications":
                        producer.produce2Dest(Symeasurename,(Medications) measureQueue.take());
                        break;
                    case "observations":
                        producer.produce2Dest(Symeasurename,(Observations) measureQueue.take());
                        break;
                    case "patients":
                        producer.produce2Dest(Symeasurename,(Patients) measureQueue.take());
                        break;
                    case "procedures":
                        producer.produce2Dest(Symeasurename,(procedures) measureQueue.take());
                        break;
                    default:
                        break;
                }
//                SRecord record = (SRecord) measureQueue.take();
//                producer.produce2Dest(Symeasurename,record);
//                logger.info("consume: " + record + ", queueSize_now: " + measureQueue.size() + ", recordNum: " + MConsumer.written.incrementAndGet());

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

