package org.mhealth.open.data.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * for DataImport
 *
 * @author just on 2017/10/10.
 */
// TODO 异步：不同Measure不同配置,还是全部同步发送
// TODO 确定后写成配置文件导入
public class ProducerSetting {
//    public static final Map<String,Properties> propsMap = new HashMap<>();
    public static Properties props = new Properties();
    private static final String BROKER_LIST = "192.168.222.226:9092";//,192.168.222.7:9092,192.168.222.9:9092,192.168.222.11:9092,192.168.222.12:9092";
    private static final String SCHEMA_REGISTRY_URL = "http://192.168.222.226:8081";
    static {
        props.put("bootstrap.servers", BROKER_LIST);//服务器{ip:port,...}
        props.put("schema.registry.url",SCHEMA_REGISTRY_URL);
        props.put("request.required.acks", "1");//有一台服务器收到就确认
        props.put("producer.type", "async");//同步
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put("partitioner.class", "org.mhealth.open.data.consumer.MKafkaProducerPartitioner");//设置分区类
        props.put("message.send.max.retries", "3");// 重试次数
        props.put("batch.num.messages", "100");// 异步提交的时候(async)，并发提交的记录数
        props.put("send.buffer.bytes", "102400");// 设置缓冲区大小，默认10KB

    }
}
