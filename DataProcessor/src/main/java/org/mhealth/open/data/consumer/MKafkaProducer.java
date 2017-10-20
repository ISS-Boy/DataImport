package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.configuration.ProducerSetting;
import org.mhealth.open.data.reader.MRecord;
import org.mhealth.open.data.avro.MMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * for DataImport
 *
 * @author just on 2017/10/11.
 */
public class MKafkaProducer implements MProducer {
    private static Logger logger = Logger.getLogger(MKafkaProducer.class);

    //    private KafkaProducer<String, GenericRecord> kafkaProducer;
    private KafkaProducer<String, MEvent> kafkaProducer;
//    private String measures; // 异步发送使用不同producer property配置

    public MKafkaProducer() {
        kafkaProducer = new KafkaProducer<String, MEvent>(ProducerSetting.props);
    }

    @Override
    public void produce2Dest(MRecord record) {
        // 消息封装成指定格式

        MEvent avroMsg = new MEvent(record.getUserId(), record.getTimestamp(), record.getMeasures());

//            GenericRecordBuilder avroBuidler = new GenericRecordBuilder(loadSchema("mevent.avsc"));
//            avroBuidler.set("user_id",record.getUserId())
//                    .set("timestamp",record.getDate().toEpochMilli())
//                    .set("measures",record.getMeasures());
//            ProducerRecord<String, GenericRecord> message = new ProducerRecord<String, GenericRecord>(record.getMeasureName(),
//                    record.getUserId(), avroBuidler.build());
        ProducerRecord<String, MEvent> message = new ProducerRecord<String, MEvent>(record.getMeasureName(),
                record.getUserId(), avroMsg);

        // non-blocking using Callback
        kafkaProducer.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败",e);
            }

            logger.info(("The offset of the record we just sent is: " + metadata.offset()));
        });

    }

    @Override
    public void close() {
        kafkaProducer.close();
    }
//    public Schema loadSchema(String name) throws IOException {
//        try (InputStream input = MKafkaProducer.class.getClassLoader()
//                .getResourceAsStream("avro/" + name)) {
//            return new Schema.Parser().parse(input);
//        }
//    }


}
