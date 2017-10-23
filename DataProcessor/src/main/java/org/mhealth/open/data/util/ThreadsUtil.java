package org.mhealth.open.data.util;

import org.mhealth.open.data.monitor.MMonitor;
import org.mhealth.open.data.monitor.Monitor;
import org.mhealth.open.data.monitor.SMonitor;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.reader.SFileReader;

import java.util.Collection;

/**
 * Created by dujijun on 2017/10/8.
 */
public class ThreadsUtil {
    /**
     * 这个方法目的是为了对不同类型的Reader进行开启的工作
     * 对于fileReader，它的开启会开启所有reader线程并使其同时开启完毕
     */
    public static void startupDataReader(Collection<MDataReader> dataReaders) throws InterruptedException {
        for (MDataReader reader : dataReaders) {

            // 这里是启动你的MFileReader的代码
            if(reader instanceof MFileReader) {
                MFileReader fileReader = (MFileReader) reader;
                fileReader.waitForThreadsStartup();
            }

            // 这里是启动你的SFileReader的代码
            else if(reader instanceof SFileReader){

            }
            // 后面还有会其它的reader类，
        }
    }

    /**
     * 这个方法目的是为了对不同类型的Reader, 进行监控的工作
     * 对于fileReader，它会监控队列并操作Reader执行操作
     * @param dataReaders 数据读取器
     * @throws InterruptedException
     */
    public static void startupMonitorAndSetReader(Collection<MDataReader> dataReaders) throws InterruptedException {
        Monitor monitor = null;
        for (MDataReader reader : dataReaders) {
            // 这里开启MHealth的监控线程
            if(reader instanceof MFileReader)
                monitor = new MMonitor();
            // 这里开启Synthea的监控线程
            else if(reader instanceof SFileReader){
                monitor = new SMonitor();
            // 这里开启...的监控线程

            }

            // 这里开启对某reader的监控线程
            monitor.startMonitor(reader);
            monitor.waitForThreadsStartup();

        }
    }


}
