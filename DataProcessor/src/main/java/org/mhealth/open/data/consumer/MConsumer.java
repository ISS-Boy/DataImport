package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * for DataImport
 *
 * @author just on 2017/10/10.
 */
public class MConsumer {
    private static Logger logger = Logger.getLogger(MConsumer.class);
    private static Logger loggerBP = Logger.getLogger("bloodPressure");
    private static Logger loggerHR = Logger.getLogger("heartRate");
    private static Logger loggerBF = Logger.getLogger("bodyFat");


    public static AtomicLong written = new AtomicLong(0);

    public void consumeData() {

        ExecutorService threadPool = Executors.newCachedThreadPool();

        //遍历队列,创建对应个数的消费者
        Application.queueMaps.forEach(
                (name, queue) -> {
                    for (int i = 0; i < ConfigurationSetting.measures.get(name).getProducerNums(); i++) {
                        // 指定数据发送到kafka终端
                MProducer producer = new MKafkaProducer();

                        // TODO 用来将不同metric的log分开,最好删掉switch,使用日志分析工具来做.
                        switch (name) {
                            case "blood-pressure":
                                threadPool.execute(new MConsumerThread(queue, producer,loggerBP));
                                break;
                            case "body-fat-percentage":
                                threadPool.execute(new MConsumerThread(queue, producer,loggerBF));
                                break;
                            case "heart-rate":
                                threadPool.execute(new MConsumerThread(queue,producer, loggerHR));
                                break;
                            default:
                                threadPool.execute(new MConsumerThread(queue,producer,logger));
                                break;
                        }
                    }
                });

        // 顺序执行已提交任务，不再接受新任务.
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
