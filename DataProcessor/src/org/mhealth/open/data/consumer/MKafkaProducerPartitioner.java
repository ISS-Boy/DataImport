package org.mhealth.open.data.consumer;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

/**
 * Created by zhangbo on 10/05.
 */
public class MKafkaProducerPartitioner implements Partitioner {

//    private Logger logger = Logger.getLogger(MKafkaProducerPartitioner.class);
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
            partitionNum = Integer.parseInt((String) key);
        } catch (Exception e) {
            partitionNum = key.hashCode();
        }
        System.out.println(("the message sendTo topic:"+ topic+" and the partitionNum:"+ partitionNum));
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

}