package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.LatitudeAndLongitude;
import org.mhealth.open.data.configuration.LaloProducerSetting;


/**
 * Created by dujijun on 2017/12/13.
 */
public class LaLoProducer {
    private static Logger logger = Logger.getLogger(LaLoProducer.class);

    private KafkaProducer<String, LatitudeAndLongitude> kafkaProducer
            = new KafkaProducer<String, LatitudeAndLongitude>(LaloProducerSetting.props);

    public void produce2Dest(LatitudeAndLongitude record) {
        ProducerRecord<String, LatitudeAndLongitude> message
                = new ProducerRecord<String, LatitudeAndLongitude>(
                LaloProducerSetting.LALO_TOPIC,
                record.getUserId(),
                record
        );

        // non-blocking using Callback
        kafkaProducer.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败", e);
            }

            logger.info(("The offset of the record we just sent is: " + metadata.offset()));
        });
    }
}
