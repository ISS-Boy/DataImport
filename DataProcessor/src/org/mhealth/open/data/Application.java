package org.mhealth.open.data;

import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.consumer.MConsumer;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MDataReaderFactory;
import org.mhealth.open.data.monitor.MMonitor;
import org.mhealth.open.data.util.ThreadsUtil;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by dujijun on 2017/10/5.
 */
public class Application {
    /**
     * 1、初始化优先队列数据结构
     * 2、开启reader线程，并等待全部开启
     * 3、开启监控队列线程，并等待全部开启
     * 4、等到reader线程全部开启之后开启处理线程
     * @param args
     */

    public static void main(String[] args) throws InterruptedException {

        Map<String, BlockingQueue> queueMaps = initRecordContainer();

        MDataReader reader = startupReaderThreadsAndWait(queueMaps);

        startupMonitorThreadAndWait(reader, queueMaps);

        startupDataExportThreads(queueMaps);

    }

    // 开启监控数据队列线程
    private static void startupMonitorThreadAndWait(MDataReader reader, Map<String, BlockingQueue> queueMaps) throws InterruptedException {
        MMonitor monitor = new MMonitor();

        // 通过这种方式来进行接耦合
        ThreadsUtil.startupMonitorAndSetReader(reader, monitor, queueMaps);
    }


    // 开启数据导出线程
    private static void startupDataExportThreads(Map<String, BlockingQueue> queueMaps) {

        // 为队列设置消费线程

        MConsumer consumer = new MConsumer();
        consumer.consumeData(queueMaps);

    }

    // 开启读数据进程并等待
    private static MDataReader startupReaderThreadsAndWait(Map<String, BlockingQueue> queueMaps) throws InterruptedException {
        MDataReaderFactory factory = new MDataReaderFactory();
        MDataReader reader = factory.getReader(ConfigurationSetting.READER_CLASS);
        reader.readDataInQueue(queueMaps);

        // 真正开启reader
        ThreadsUtil.startupDataReader(reader);
        return reader;
    }

    // 初始化记录数据的容器
    private static Map<String, BlockingQueue> initRecordContainer() {
        // 现在先通过简单的方式获取消息容器
        return ConfigurationSetting.getSimpleContainer();
    }
}
