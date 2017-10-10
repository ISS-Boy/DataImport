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

    public MConsumerThread(BlockingQueue queueMaps) {
        this.queueMaps = queueMaps;

    }

    @Override
    public void run() {
//        Producer producer = initKafkaProducer();
        try{
            while(true){
                MRecord record = (MRecord) queueMaps.take();
                System.out.println("消费了数据"+record);
//                sendToKafka(producer,new ProducerRecord<>(
//                        record.getMeasureName(),
//                        "user_id:"+record.getUserId()+",timestamp:"+record.getTimestamp(),record.getMsg()));
                if(record.isPoisonPill())
                    break;
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public Producer<String ,String> initKafkaProducer(){

        Producer<String,String> kafkaProducer = new KafkaProducer<>(ProducerSetting.props);
        return kafkaProducer;

    }
    public void sendToKafka(Producer<String,String> producer,ProducerRecord<String,String> message) {
        // non-blocking using Callback
        producer.send(message,(RecordMetadata metadata, Exception e)->{
            if(e != null)
                e.printStackTrace();
            System.out.println(("The offset of the record we just sent is: " + metadata.offset()));
        });
    }
}
