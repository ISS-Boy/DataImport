package org.mhealth.open.data.consumer;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.*;

import java.util.List;
import java.util.Map;

public class SKafkaProducerPartitioner implements Partitioner {

    private static final long MILLIS_OF_DAY = 5 * 60 * 1000L;
    private Logger logger = Logger.getLogger(SKafkaProducerPartitioner.class);

    public SKafkaProducerPartitioner() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int partitionNum = 0;
        try {
            String s= value.getClass().getName();
            if(value instanceof SAllergie){
                partitionNum = (int) ((long) ((SAllergie) value).get("timestamp") / MILLIS_OF_DAY);//还要加一步，取天
            }
            else if(value instanceof SPatient){
                partitionNum = (int) ((long) ((SPatient) value).get("timestamp") / MILLIS_OF_DAY);//还要加一步，取天
            }
            else if(value instanceof SObservation){
                partitionNum = (int) ((long) ((SObservation) value).get("timestamp") / MILLIS_OF_DAY);//还要加一步，取天
            }
            else {
                partitionNum = (int) ((long) ((SLine) value).get("timestamp") / MILLIS_OF_DAY);//还要加一步，取天
            }
        } catch (Exception e) {
            if(value instanceof SAllergie){
                partitionNum = ((SAllergie) value).get("timestamp").hashCode();
            }
            else if(value instanceof SPatient){
                partitionNum = ((SPatient) value).get("timestamp").hashCode();
            }
            else if(value instanceof SObservation){
                partitionNum = ((SObservation) value).get("timestamp").hashCode();
            }
            else {
                partitionNum = ((SLine) value).get("timestamp").hashCode();
            }
        }
        logger.info("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum % numPartitions);
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

}