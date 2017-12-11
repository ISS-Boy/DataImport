package org.mhealth.open.data;

import org.apache.log4j.Logger;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.consumer.MConsumer;
import org.mhealth.open.data.consumer.SConsumer;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MDataReaderFactory;
import org.mhealth.open.data.monitor.MMonitor;
import org.mhealth.open.data.reader.SFileReader;
import org.mhealth.open.data.util.ThreadsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


/**
 * Created by dujijun on 2017/10/5.
 */
public class Application {
    /**
     * 1、初始化优先队列数据结构(静态方法中进行构造)
     * 2、开启reader线程，并等待全部开启
     * 3、开启监控队列线程，并等待全部开启
     * 4、等到reader线程全部开启之后开启处理线程
     * @param args
     */
    private static Logger logger = Logger.getLogger(Application.class);
    public static Map<String, BlockingQueue> queueMaps = ConfigurationSetting.getSimpleContainer();
    public static Map<String, BlockingQueue> squeueMaps = ConfigurationSetting.getSyntheaContainer();
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("user.timezone","GMT");
        // In order to start producer first
        startupDataExportThreads();

        Collection<MDataReader> readers = startupReaderThreadsAndWait();

        startupMonitorThreadsAndWait(readers);


        //Synthea数据读入数据到队列，再出队到文件测试

//        startupSReaderThreads();
//        startupSDataExportThreads();

    }
    // 开启读数据进程并等待
    private static Collection<MDataReader> startupReaderThreadsAndWait() throws InterruptedException {
        MDataReaderFactory factory = new MDataReaderFactory();
        // 启动MHealthReader
        MDataReader mHealthReader = factory.getReader(ConfigurationSetting.MHEALTH_READER_CLASS);
        mHealthReader.readDataInQueue();

        // 启动SyntheaReader
        MDataReader syntheaReader = factory.getReader(ConfigurationSetting.SYNTHEA_READER_CLASS);
        syntheaReader.readDataInQueue();

        Collection<MDataReader> dataReaders = Arrays.asList(mHealthReader, syntheaReader);
//        Collection<MDataReader> dataReaders = Arrays.asList( syntheaReader);

        // 真正开启reader
        logger.info("start reading");
        ThreadsUtil.startupDataReader(dataReaders);
        return dataReaders;
    }


    // 开启监控数据队列线程
    private static void startupMonitorThreadsAndWait(Collection<MDataReader> readers) throws InterruptedException {
        // 打开读监控线程并监控读进程
        ThreadsUtil.startupMonitorAndSetReader(readers);
    }


    // 开启数据导出线程
    private static void startupDataExportThreads() {
        // 为队列设置消费线程
        new Thread (()-> new MConsumer().consumeData()).start();
        new Thread (()-> new SConsumer().consumeData()).start();
        logger.info("开启消费者线程");

    }

    //Synthea测试

//    private static void startupSReaderThreads() throws InterruptedException {
//        MDataReaderFactory factory = new MDataReaderFactory();
//        // 启动SyntheaReader
//        MDataReader syntheaReader = factory.getReader(ConfigurationSetting.SYNTHEA_READER_CLASS);
//        syntheaReader.readDataInQueue();
//
//        // 真正开启reader
//        logger.info("start reading");
//        SFileReader reader = (SFileReader)syntheaReader;
//        reader.waitForThreadsStartup();
//        reader.waitForThreadsShutdown();
//    }
//
//    private static void startupSDataExportThreads() {
//        // 为队列设置消费线程
//        SConsumer sconsumer = new SConsumer();
//        sconsumer.consumeData();
//        logger.info("关闭消费者线程");
//
//    }

}
