package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.record.SRecord;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SKafkaProducer implements SProducer{
    private static Logger logger = Logger.getLogger(SKafkaProducer.class);
    private KafkaProducer<String, MEvent> kafkaProducer;
    @Override
    public void produce2Dest(SRecord record) {
        //封装成指定格式

    }
    @Override
    public void close() {
        kafkaProducer.close();
    }
}
