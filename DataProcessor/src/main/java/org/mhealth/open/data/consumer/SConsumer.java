package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.record.Patients;
import org.mhealth.open.data.record.SRecord;

import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SConsumer {
    private static Logger logger = Logger.getLogger(SConsumer.class);


    public static AtomicInteger written = new AtomicInteger(0);

    public void consumeData() {

        ExecutorService threadPool = Executors.newCachedThreadPool();

//        遍历队列,创建对应个数的消费者
        Application.squeueMaps.forEach(
                (name, queue) -> {
                  //指定数据发送到kafka终端
                SProducer producer = new SKafkaProducer(name);
                threadPool.execute(new SConsumerThread(queue, producer, logger));
                    //指定数据写入文件
//                SProducer producer = new SFileProducer(name);
                });


//         顺序执行已提交任务，不再接受新任务.
        threadPool.shutdown();

        // 任务执行结束或时间到期时关闭
        try {
            threadPool.awaitTermination(1L, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info(written);
        }
    }

}
