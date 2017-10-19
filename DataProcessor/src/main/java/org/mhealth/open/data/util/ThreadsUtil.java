package org.mhealth.open.data.util;

import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MFileReader;
import org.mhealth.open.data.monitor.MMonitor;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by dujijun on 2017/10/8.
 */
public class ThreadsUtil {
    /**
     * 这个方法目的是为了对不同类型的Reader进行开启的工作
     * 对于fileReader，它的开启会开启所有reader线程并使其同时开启完毕
     */
    public static void startupDataReader(MDataReader reader) throws InterruptedException {
        if(reader instanceof MFileReader) {
            MFileReader fileReader = (MFileReader) reader;
            fileReader.waitForThreadsStartup();
        }
        // 后面还有会其它的reader类，
    }

    /**
     * 这个方法目的是为了对不同类型的Reader, 进行监控的工作
     * 对于fileReader，它会监控队列并操作Reader执行操作
     */
    public static void startupMonitorAndSetReader(MDataReader reader, MMonitor mMornitor, Map<String, BlockingQueue> queueMaps) throws InterruptedException {
        if(reader instanceof MFileReader){
            MFileReader fileReader = (MFileReader) reader;
            mMornitor.startMonitor(fileReader, queueMaps);
            mMornitor.waitForThreadsStartup();
        }
        //后面还会有别的reader类进行监控回调操作，
    }


}
