package org.mhealth.open.data;

import org.apache.log4j.Logger;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.consumer.MConsumer;
import org.mhealth.open.data.consumer.SConsumer;
import org.mhealth.open.data.reader.LatitudeAndLongitudeReader;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MDataReaderFactory;
import org.mhealth.open.data.util.ThreadsUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 * Created by dujijun on 2017/10/5.
 */
public class Application {
    /**
     * 1、初始化优先队列数据结构(静态方法中进行构造)
     * 2、开启reader线程，并等待全部开启
     * 3、开启监控队列线程，并等待全部开启
     * 4、等到reader线程全部开启之后开启处理线程
     *
     * @param args
     */
    private static Logger logger = Logger.getLogger(Application.class);
    public static Map<String, BlockingQueue> queueMaps = ConfigurationSetting.getSimpleContainer();
    public static Map<String, BlockingQueue> squeueMaps = ConfigurationSetting.getSyntheaContainer();

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, TimeoutException {
        System.setProperty("user.timezone", "GMT+8");
        // In order to start producer first
        startupDataExportThreads();

        Collection<MDataReader> readers = startupReaderThreadsAndWait();

        startupMonitorThreadsAndWait(readers);

        // 启动发送经纬度信息线程, 从此时开始每隔一分钟发送一次所有用户的位置信息
        // 这里使用Clock的起始时间作为最开始经纬度记录的时间戳，并且在这个基础上，
        // 每次（隔一分钟一次）发送时在上次发送时间的基础上再加上一分钟
        // 细节：
        // 1、因为mhealth的数据是对齐到分钟上的，所以不需要手工同步记录的时间
        // 只需要保证mhealth数据和经纬度数据的时间是在整分钟上的，就自然能对齐了
        // 2、因为经纬度信息可能会比mhealth的信息要多，因为它只保证数据的时间对齐
        // 到整分钟上，并没有规定何时停止，不过只要在stream连接的时候用mhealth和
        // 经纬度做左外连接就ok了。
        LatitudeAndLongitudeReader.readInitialPos();

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

        // synthea静态数据目前已持久化在kafka中,可以关闭synthea导入
        // 启动SyntheaReader
//        MDataReader syntheaReader = factory.getReader(ConfigurationSetting.SYNTHEA_READER_CLASS);
//        syntheaReader.readDataInQueue();

//        Collection<MDataReader> dataReaders = Arrays.asList(mHealthReader, syntheaReader);
        Collection<MDataReader> dataReaders = Arrays.asList(mHealthReader);

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
        new Thread(() -> new MConsumer().consumeData()).start();
//        new Thread (()-> new SConsumer().consumeData()).start();
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
