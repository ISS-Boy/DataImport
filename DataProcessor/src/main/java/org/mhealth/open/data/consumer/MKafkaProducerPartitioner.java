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

    private Logger logger = Logger.getLogger(MKafkaProducerPartitioner.class);



    @Override
    public void configure(Map<String, ?> configs) {
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int partitionNum = 0;
        int userCode ;
        try {
            MEvent e = ((MEvent) value);
            userCode = Math.abs(e.getUserId().hashCode());
            partitionNum = userCode ;
        }  catch (Exception e) {
            logger.error(e);
            partitionNum = ((MEvent) value).get("user_id").hashCode();
        }
        logger.info("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum % numPartitions);
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

}