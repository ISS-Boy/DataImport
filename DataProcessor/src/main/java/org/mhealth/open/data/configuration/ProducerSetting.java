package org.mhealth.open.data.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        try {
            props.put("bootstrap.servers", BROKER_LIST);//服务器{ip:port,...}
            props.put("client.id", InetAddress.getLocalHost().getHostName()); // 本地ip,方便调试

            props.put("schema.registry.url", SCHEMA_REGISTRY_URL);
            props.put("acks", "1");//有一台服务器写入成功就确认,需要强guarantee则配置为"all"
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
            props.put("partitioner.class", "org.mhealth.open.data.consumer.MKafkaProducerPartitioner");//设置分区类
            props.put("retries", "3");// 重试次数，会触发kafka的reordering
            props.put("buffer.memory", "33554432");// 缓冲区的大小限制
            props.put("batch.size", "102400");// 异步提交的时候(async)，并发提交的记录batch,单位是byte
//            props.put("linger.ms", "5"); // 延迟时间，若已达到batch.size会无视该配置直接发送

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
