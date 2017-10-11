package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.mhealth.open.data.configuration.ProducerSetting;
import org.mhealth.open.data.reader.MRecord;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * for DataImport
 *
 * @author just on 2017/10/9.
 */
public class MConsumerThread implements Runnable{
    private BlockingQueue queueMaps;
    private MProducer producer;

    public MConsumerThread(BlockingQueue queueMaps, MProducer producer) {
        this.queueMaps = queueMaps;
        this.producer = producer;
    }


    @Override
    public void run() {
        try{
            while(true){
                MRecord record = (MRecord) queueMaps.take();
                if(record.isPoisonPill())
                    break;
                producer.produce2Dest(record);
                System.out.println("消费了数据"+record);

            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }


}
