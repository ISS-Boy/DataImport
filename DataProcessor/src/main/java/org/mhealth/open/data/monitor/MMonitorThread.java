package org.mhealth.open.data.monitor;

import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.configuration.MeasureConfiguration;
import org.mhealth.open.data.reader.AbstractMThread;
import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.reader.MFileReaderThread;
import org.mhealth.open.data.reader.MRecord;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MMonitorThread extends MonitorThread {

    private final MFileReader reader;

    MMonitorThread(MFileReader reader, CountDownLatch startupLatch) {
        super(startupLatch);
        this.reader = reader;
    }

    // 这里开始监控主逻辑
    protected void monitor() throws InterruptedException {
        // 首先监控所有队列是否满足数据读入阈值需求，
        //  如果满足，则看Reader对象中的线程是否还处于读数据的状态
        //      如果Reader中的线程还处于读数据的状态，则sleep一段时间后在观察队列情况
        //      如果Reader中的线程已经处于静默状态，则将达到读入要求的队列标记为true并将这些信息传递给Reader中线程，并唤醒它们开始输入数据
        //  如果不满足则继续sleep一段时间后再进行观测

        Map<String, MeasureConfiguration> measures = ConfigurationSetting.measures;
        Map<String, Boolean> needImportMeasure = new HashMap<>();
        while (true) {
            while (!reader.isAllEnd()) {

                // 等待读取完成之后
                // , 这里应该需要阻塞一会
                reader.waitForThreadsWorkDown();

                Application.queueMaps.forEach((s, v) -> {
                    // 如果属于MHealth的数据
                    if (measures.keySet().contains(s)) {
                        float currentSize = v.size();
                        float rate = currentSize / ConfigurationSetting.MAX_QUEUE_SIZE;

                        // 当目前元素的比率小于阈值时，则判断需要导入数据
                        needImportMeasure.put(s, rate < measures.get(s).getQueueImportThreshold());
                    }
                });

                // 重置完毕锁
                CountDownLatch completeLatch = new CountDownLatch(reader.CURRENT_READER_COUNT.get());
                reader.resetCompleteLatchs(completeLatch);
                reader.setCompleteLatch(completeLatch);

                // 设置需要读取tag
                reader.setTagAndWaitupThreadsToReadData(needImportMeasure);
                Thread.sleep(1000);
            }

            reader.CURRENT_READER_COUNT.set(ConfigurationSetting.READER_COUNT.get());
            ConfigurationSetting.repeat++;
            for (MFileReaderThread task : reader.getReaderThreads()) {
                synchronized (task) {
                    task.notify();
                }
            }
            // 保证唤醒所有线程再进行后续操作
            while (!reader.isAllBlocking()) ;
        }
//        queueMaps.forEach(
//                (measureName, queue) -> {
//                    for (int i = 0; i < ConfigurationSetting.measures.get(measureName).getProducerNums(); i++)
//                        queue.offer(new MRecord(true, Instant.parse(ConfigurationSetting.END_TIME)));
//
//                });
    }
}
