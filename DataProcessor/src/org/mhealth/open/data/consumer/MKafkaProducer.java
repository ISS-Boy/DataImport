package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.configuration.ProducerSetting;
import org.mhealth.open.data.reader.MRecord;

/**
 * for DataImport
 *
 * @author just on 2017/10/11.
 */
public class MKafkaProducer implements MProducer {
    private KafkaProducer<String,String> kafkaProducer;
//    private String measures; // 异步发送使用不同producer property配置

    public MKafkaProducer() {
        kafkaProducer = new KafkaProducer<String, String>(ProducerSetting.props);
    }

    @Override
    public void produce2Dest(MRecord record) {
        // 消息封装成指定格式
        ProducerRecord<String ,String> message = new ProducerRecord<String, String>(record.getMeasureName(),
                record.getUserId()+record.getTimestamp(),record.getMsg()
                );

        // non-blocking using Callback
        kafkaProducer.send(message,(RecordMetadata metadata, Exception e)->{
            if(e != null)
                e.printStackTrace();
            System.out.println(("The offset of the record we just sent is: " + metadata.offset()));
        });

    }


}
