package org.mhealth.open.data.consumer;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.*;

import java.util.List;
import java.util.Map;

public class SKafkaProducerPartitioner implements Partitioner {

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
            if (value instanceof SAllergie) {
                partitionNum =((SAllergie) value).getUserId().hashCode();
            } else if (value instanceof SPatient) {
                partitionNum = ((SPatient) value).getUserId().hashCode();
            } else if (value instanceof SObservation) {
                partitionNum =  ((SObservation) value).getUserId().hashCode();
            } else {
                partitionNum = ((SLine) value).getUserId().hashCode();
            }
        } catch (Exception e) {
           logger.error(e);
        }
        logger.info("the message sendTo topic:" + topic + " and the partitionNum:" + Math.abs(partitionNum % numPartitions));
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

}