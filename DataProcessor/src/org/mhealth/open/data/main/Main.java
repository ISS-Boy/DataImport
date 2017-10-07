package org.mhealth.open.data.main;

import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.reader.MMornitor;

import java.util.Map;
import java.util.Queue;

/**
 * Created by dujijun on 2017/10/5.
 */
public class Main {
    /**
     * 1、初始化优先队列数据结构
     * 2、开启reader线程，并等待全部开启
     * 3、开启监控队列线程，并等待全部开启
     * 4、等到reader线程全部开启之后开启处理线程
     * 5、一轮数据读取完毕后，重置相关状态，重用数据进行下一轮读取
     * @param args
     */

    public static void main(String[] args) throws InterruptedException {

        Map<String, Queue> queueMaps = initRecordContainer();

        startupReaderThreadsAndWait(queueMaps);

        startupMonitorThreadAndWait(queueMaps);

        startupDataExportThreads(queueMaps);

    }

    // 开启监控数据队列线程
    private static void startupMonitorThreadAndWait(Map<String, Queue> queueMaps) throws InterruptedException {
        MMornitor monitor = new MMornitor();
        monitor.startMonitor(queueMaps);
        monitor.waitForThreadsStartup();
    }


    // 开启数据导出线程
    private static void startupDataExportThreads(Map<String, Queue> queueMaps) {

    }

    // 开启读数据进程并等待
    private static void startupReaderThreadsAndWait(Map<String, Queue> queueMaps) throws InterruptedException {
        MDataReader reader = new MFileReader();
        reader.readDataInQueue(queueMaps);
        ((MFileReader) reader).waitForThreadsStartup();
    }

    // 初始化记录数据的容器
    private static Map<String, Queue> initRecordContainer() {
        // 现在先通过简单的方式获取消息容器
        return ConfigurationSetting.getSimpleContainer();
    }
}
