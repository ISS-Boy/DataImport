package org.mhealth.open.data.consumer;

import org.apache.log4j.Logger;
import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SConsumer {
    private static Logger logger = Logger.getLogger(SConsumer.class);
    private static Logger loggerAL = Logger.getLogger("allergies");
    private static Logger loggerCA = Logger.getLogger("careplans");
    private static Logger loggerCO = Logger.getLogger("conditions");
    private static Logger loggerEN = Logger.getLogger("encounters");
    private static Logger loggerIM = Logger.getLogger("immunizations");
    private static Logger loggerME = Logger.getLogger("medications");
    private static Logger loggerOB = Logger.getLogger("observations");
    private static Logger loggerPA = Logger.getLogger("patients");
    private static Logger loggerPR = Logger.getLogger("procedures");



    public static AtomicInteger written = new AtomicInteger(0);

    public void consumeData() {

        ExecutorService threadPool = Executors.newCachedThreadPool();

        //遍历队列,创建对应个数的消费者
        Application.squeueMaps.forEach(
                (name, queue) -> {
                  //指定数据发送到kafka终端
//                SProducer producer = new SKafkaProducer();
                    //指定数据写入文件
                SProducer producer = new SFileProducer(name);
//                threadPool.execute(new MConsumerThread(queueMaps.get(measureName), producer));
                        switch (name) {
                            case "allergies":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerAL));
                                break;
                            case "careplans":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerCA));
                                break;
                            case "conditions":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerCO));
                                break;
                            case "encounters":
                                threadPool.execute(new SConsumerThread(queue,producer, loggerEN));
                                break;
                            case "immunizations":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerIM));
                                break;
                            case "medications":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerME));
                                break;
                            case "observations":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerOB));
                                break;
                            case "patients":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerPA));
                                break;
                            case "procedures":
                                threadPool.execute(new SConsumerThread(queue, producer, loggerPR));
                                break;
                            default:
                                break;
                        }
                });
        // 顺序执行已提交任务，不再接受新任务.
        threadPool.shutdown();

        // 任务执行结束或时间到期时关闭
        try {
            threadPool.awaitTermination(7L, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info(written);
        }
    }

}
