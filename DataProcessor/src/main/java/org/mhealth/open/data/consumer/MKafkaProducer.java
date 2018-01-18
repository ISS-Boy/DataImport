package org.mhealth.open.data.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.avro.Measure;
import org.mhealth.open.data.configuration.ProducerSetting;
import org.mhealth.open.data.reader.MRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * for DataImport
 *
 * @author just on 2017/10/11.
 */
public class MKafkaProducer implements MProducer {
    private static Logger logger = Logger.getLogger(MKafkaProducer.class);

    private KafkaProducer<String, MEvent> kafkaProducer;
//    private String measures; // 异步发送使用不同producer property配置

    public MKafkaProducer() {
        kafkaProducer = new KafkaProducer<String, MEvent>(ProducerSetting.props);
    }

    @Override
    public void produce2Dest(MRecord record) {
        // 消息封装成指定格式

        MEvent event = record.getEvent();


        ProducerRecord<String, MEvent> message = new ProducerRecord<String, MEvent>(record.getMeasureName(),
                event.getUserId(), event);

        // non-blocking using Callback
        kafkaProducer.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败", e);
            }

            logger.info((String.format("The offset of %s we just sent is: %s",metadata.topic(),metadata.offset())));
        });

    }


    @Override
    public void close() {
        kafkaProducer.close();
    }


}
