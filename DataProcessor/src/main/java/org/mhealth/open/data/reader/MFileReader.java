package org.mhealth.open.data.reader;

import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.exception.InValidPathException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mhealth.open.data.Application.queueMaps;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MFileReader extends MThreadController implements MDataReader {

    /**
     * 需要改进到从配置文件中读入
     */
    private String dataRootPath = ConfigurationSetting.DATA_ROOT_PATH;
    private List<MFileReaderThread> readers;
    public final AtomicInteger CURRENT_READER_COUNT = new AtomicInteger(0);


    public MFileReader() {
        readers = new ArrayList<>();
    }

    public List<MFileReaderThread> getReaderThreads() {
        return readers;
    }

    @Override
    public void readDataInQueue() {
        File rootDir = new File(dataRootPath);
        if (!rootDir.isDirectory())
            throw new InValidPathException("数据路径选取不合法，请重新选择路径");

        File[] userGroups = rootDir.listFiles(File::isDirectory);

        // 设置全局读取线程个数，它用来判断设置毒丸结束，还可以用来计算一轮读取完毕锁
        int threadsCount = userGroups.length;
        ConfigurationSetting.READER_COUNT.set(threadsCount);
        CURRENT_READER_COUNT.set(threadsCount);

        // 初始化闭锁
        CountDownLatch startupThreadsLatch = new CountDownLatch(userGroups.length);
        CountDownLatch readCompleteLatch = new CountDownLatch(userGroups.length);

        // 设置闭锁
        setStartupLatch(startupThreadsLatch);
        setCompleteLatch(readCompleteLatch);
        for (File userGroup : userGroups) {
            MFileReaderThread reader = new MFileReaderThread(startupThreadsLatch, readCompleteLatch, userGroup, CURRENT_READER_COUNT);
            Thread readThread = new Thread(reader);
            readers.add(reader);
            readThread.start();
        }
    }

    public void setTagAndWaitupThreadsToReadData(Map<String, Boolean> tags) {

        readers.forEach(r -> {
                    if (!r.isEnd())
                        r.setTags(tags);
                }
        );
    }

    public boolean isAllEnd() {
        for (MFileReaderThread readerThread : readers) {
            if (!readerThread.isEnd())
                return false;
        }
        return true;
    }


    public void resetCompleteLatchs(CountDownLatch completeLatch) {
        readers.forEach(t -> t.resetCompleteLatch(completeLatch));
    }
}
