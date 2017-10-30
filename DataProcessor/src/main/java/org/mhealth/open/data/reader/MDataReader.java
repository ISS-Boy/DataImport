package org.mhealth.open.data.reader;

/**
 * Created by dujijun on 2017/10/5.
 */

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * 负责读取数据
 */
public interface MDataReader {

    /**
     * 将数据读入目标队列当中
     */
    void readDataInQueue() throws InterruptedException;
}
