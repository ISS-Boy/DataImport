package org.mhealth.open.data.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.*;
import org.mhealth.open.data.configuration.SProducerSetting;
import org.mhealth.open.data.record.*;

import java.time.Instant;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SKafkaProducer implements SProducer{
    private static Logger logger = Logger.getLogger(SKafkaProducer.class);
    private KafkaProducer<String, SAllergie> kafkaProducer1 = null;
    private KafkaProducer<String, SObservation> kafkaProducer2 = null;
    private KafkaProducer<String, SPatient> kafkaProducer3 = null;
    private KafkaProducer<String, SLine> kafkaProducer4 = null;

    public SKafkaProducer(String name) {
        if(name.equals("allergies")){
            kafkaProducer1 = new KafkaProducer<String, SAllergie>(SProducerSetting.props);
        }
        else if(name.equals("observations")){
            kafkaProducer2 = new KafkaProducer<String, SObservation>(SProducerSetting.props);
        }
        else if(name.equals("patients")){
            kafkaProducer3 = new KafkaProducer<String, SPatient>(SProducerSetting.props);
        }
        else {
            kafkaProducer4 = new KafkaProducer<String, SLine>(SProducerSetting.props);
        }
    }

    @Override
    public void produce2Dest(Allergies record) {
        SAllergie avroMsg = new SAllergie(record.getTimestamp(),record.getUserId(),record.getStart().toString(), record.getEncounter(),record.getAllergies());
        ProducerRecord<String, SAllergie> message = new ProducerRecord<String, SAllergie>("allergies",
                record.getUserId(), avroMsg);
        kafkaProducer1.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败",e);
            }
            logger.info(("The offset of Allergies we just sent is: " + metadata.offset()));
        });

    }

    @Override
    public void produce2Dest(Observations record) {
        SObservation avroMsg = new SObservation(record.getTimestamp(),record.getUserId(),record.getDate().toString(),record.getEncounter(),record.getSigns());
        ProducerRecord<String, SObservation> message = new ProducerRecord<String, SObservation>("observation",
                record.getUserId(), avroMsg);
        kafkaProducer2.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败",e);
            }
            logger.info(("The offset of Observation we just sent is: " + metadata.offset()));
        });

    }

    @Override
    public void produce2Dest(Patients record) {
        SPatient avroMsg = new SPatient(record.getTimestamp(),record.getUserId(),record.getName(),record.getBirthdate().toString(),(record.getDeathdate()==null? null:record.getDeathdate().toString()),record.getGender(),record.getRace());
        ProducerRecord<String, SPatient> message = new ProducerRecord<String, SPatient>("patient",
                record.getUserId(), avroMsg);
        kafkaProducer3.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败",e);
            }
            logger.info(("The offset of Patient we just sent is: " + metadata.offset()));
        });

    }

    @Override
    public void produce2Dest(SRecord record) {
        //封装成指定格式
        ProducerRecord<String, SLine> message=null;
        SLine avroMsg = new SLine(record.getTimestamp(),record.getUserId(),(record.getDate()==null? null:record.getDate().toString()),record.getCode(),record.getRcode(),(record.getStart()==null? null:record.getStart().toString())
                ,(record.getStop()==null? null:record.getStop().toString()),record.getEncounter(),record.getDescription(),record.getReasondescription());
        if(record instanceof CarePlans){

             message = new ProducerRecord<String, SLine>("careplans",
                record.getUserId(), avroMsg);
        }

        if(record instanceof Conditions){
            message = new ProducerRecord<String, SLine>("conditions",
                    record.getUserId(), avroMsg);
        }

        if(record instanceof Encounters){
            message = new ProducerRecord<String, SLine>("encounters",
                    record.getUserId(), avroMsg);
        }

        if(record instanceof Immunizations){
            message = new ProducerRecord<String, SLine>("immunizations",
                    record.getUserId(), avroMsg);
        }

        if(record instanceof Medications){
            message = new ProducerRecord<String, SLine>("medications",
                    record.getUserId(), avroMsg);
        }

        if(record instanceof procedures){
            message = new ProducerRecord<String, SLine>("procedures",
                    record.getUserId(), avroMsg);
        }



        kafkaProducer4.send(message, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                logger.error("发送消息失败",e);
            }
            logger.info(("The offset of syn we just sent is: " + metadata.offset()));
        });
    }

    @Override
    public void close() {
        if(kafkaProducer1!=null)
            kafkaProducer1.close();
        if(kafkaProducer2!=null)
            kafkaProducer2.close();
        if(kafkaProducer3!=null)
            kafkaProducer3.close();
        if(kafkaProducer4!=null)
            kafkaProducer4.close();
    }
}
