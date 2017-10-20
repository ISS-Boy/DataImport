package org.mhealth.open.data.consumer;


import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.log4j.Logger;
import org.mhealth.open.data.avro.MEvent;

/**
 * Created by zhangbo on 10/05.
 */
public class MKafkaProducerPartitioner implements Partitioner {

    private static final long MILLIS_OF_DAY = 5 * 60 * 1000L;
    private Logger logger = Logger.getLogger(MKafkaProducerPartitioner.class);

    public MKafkaProducerPartitioner() {
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
            partitionNum = (int) ((long) ((MEvent) value).get("timestamp") / MILLIS_OF_DAY);//还要加一步，取天

        } catch (Exception e) {
            partitionNum = ((MEvent) value).get("timestamp").hashCode();
        }
        logger.info("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum % numPartitions);
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

}