package org.mhealth.open.data.configuration;

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
    private static final String BROKER_LIST = "192.168.222.6:9092,192.168.222.7:9092,192.168.222.9:9092,192.168.222.11:9092,192.168.222.12:9092";
    static {
        props.put("bootstrap.servers", BROKER_LIST);//服务器{ip:port,...}
        props.put("request.required.acks", "1");//有一台服务器收到就确认
        props.put("producer.type", "sync");//同步
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("partitioner.class", "MKafkaProducerPartitioner");//设置分区类
        props.put("message.send.max.retries", "3");// 重试次数
        //props.put("batch.num.messages", "200");// 异步提交的时候(async)，并发提交的记录数
        props.put("send.buffer.bytes", "102400");// 设置缓冲区大小，默认10KB

    }
}
