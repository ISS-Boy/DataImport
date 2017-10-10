package org.mhealth.open.data.consumer;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * for DataImport
 *
 * @author just on 2017/10/10.
 */
public class MConsumer {
    private final Set<String> measures = ConfigurationSetting.measures.keySet();

    public void consumeData(Map<String,BlockingQueue> queueMaps){

        ExecutorService threadPool = Executors.newCachedThreadPool();

        for (String measureName : measures) {

            int producerNums = ConfigurationSetting.measures.get(measureName).getProducerNums();
            for (int i = 0; i < producerNums; i++) {
                threadPool.execute(new MConsumerThread(queueMaps.get(measureName)));
            }

        }
        // from javadoc:This method does not wait for previously submitted tasks to complete execution.
        threadPool.shutdown();
        try {
            // 任务执行结束或时间到期时关闭
            threadPool.awaitTermination(7L, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
