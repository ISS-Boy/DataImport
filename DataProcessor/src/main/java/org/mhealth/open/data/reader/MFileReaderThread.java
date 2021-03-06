package org.mhealth.open.data.reader;

import org.apache.log4j.Logger;
import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.exception.UnhandledQueueOperationException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by dujijun on 2017/10/5.
 */
public class MFileReaderThread extends AbstractMThread {
    private static Logger logger = Logger.getLogger(MFileReaderThread.class);
    private File userGroupDir;

    private Map<String, Long> fileOffsetRecorder = new HashMap<>();
    private Map<String, Boolean> tags = new HashMap<>();
    private final AtomicInteger THREADS_COUNT;
    private volatile boolean end = false;
    private volatile int finishFileCount = 0;
    private volatile AtomicBoolean blocking = new AtomicBoolean(false);

    public MFileReaderThread(CountDownLatch startupLatch, CountDownLatch readCompleteLatch, File userGroupDir, AtomicInteger THREADS_COUNT) {
        super(startupLatch, readCompleteLatch);
        this.userGroupDir = userGroupDir;
        this.THREADS_COUNT = THREADS_COUNT;

        // 先设置为全真
        Set<String> measureNames = ConfigurationSetting.measures.keySet();
        measureNames.forEach(name -> tags.put(name, true));
    }

    private void readUserGroupDataInQueue() throws InterruptedException {
        // 1、获取用户组文件目录对象，遍历文件目录下所有的用户
        // 2、获取用户目录下的文件名称，并使用[用户名-度量名]作为标识文件偏移的key
        // 3、获取当前文件的偏移，并从此偏移开始向下读取n条记录到对应的队列当中
        // 4、读取完毕后记录文件偏移，以备下次读取

        File[] users = userGroupDir.listFiles(File::isDirectory);

        for (File user : users) {

            // 获取用户名
            String userName = user.getName();
            for (File measure : user.listFiles()) {
                String measureName = measure.getName();

                // 两种方式都能得到正确的measure
                measureName = measureName.replaceAll("-output\\.json", "");
//                measureName = measureName.substring(0, measureName.indexOf("."));

                // 获取measure队列
                Queue measureQueue = Objects.requireNonNull(Application.queueMaps.get(measureName), "队列未创建或文件名有误");

                // 开始正式读取文件
                // 获取文件对应的起始offset指针
                long startOffset;
                String offsetKey = userName + "-" + measureName;

                // 如果这次读取数据文件是不允许读此测量值文件的，则跳过此次读取
                if (!tags.get(measureName))
                    continue;

                // 获取上一次读取文件的指针位置
                if (!fileOffsetRecorder.containsKey(offsetKey)) {
                    startOffset = 0L;
                    fileOffsetRecorder.put(offsetKey, 0L);
                } else {
                    long offset = fileOffsetRecorder.get(offsetKey);
                    // 如果文件已经读完，则不需要再读
                    if (offset == -1)
                        continue;
                    else
                        startOffset = offset;
                }

                try (RandomAccessFile raf = new RandomAccessFile(measure, "r")) {
                    // 初始化文件offset指针位置
                    raf.seek(startOffset);
                    String record = null;
                    long nextStartOffset = startOffset;
                    int frequency = ConfigurationSetting.measures.get(measureName).getReadingFrequency();
                    logger.info(String.format("start to read data from: %s in %s",measureName,userGroupDir.getName()));
                    for (int i = 0; i < frequency && (record = raf.readLine()) != null; i++) {

                        // 这里应该有对应record的处理过程, 这里会有两种处理方式
                        // 1、直接当作字符串 ☑️
                        // 2、转换成对象来进行处理
                        MRecord mRecord = new MRecord(record, measureName);
                        if (!measureQueue.offer(mRecord)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }

                    // 更新下一次读文件的StartOffset
                    nextStartOffset = raf.getFilePointer();

                    // 如果已经读到底了，则下次不应该再读
                    if (record == null) {
                        nextStartOffset = -1;
                        finishFileCount++;
                    }
                    fileOffsetRecorder.put(offsetKey, nextStartOffset);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 每次读一次文件，都应该判断是否全部读完
        end = finishFileCount == fileOffsetRecorder.keySet().size();
        tags.forEach((s, b) -> {
            if (b)
                logger.info("成功读取了一批"+userGroupDir.getName()+"-"+s+"的文件");
        });

    }

    public boolean isEnd() {
        return end;
    }

    // 设置tags并将阻塞状态置为false
    public void setTags(Map<String, Boolean> tags) {
        this.tags = tags;
        while (!blocking.compareAndSet(true, false)) ;
    }

    public boolean isBlocking() {
        return blocking.get();
    }

    @Override
    public void run() {
        startupComplete();

        //每次都将用户组目录下的数据读入队列中
        try {
            while (true) {

                while (blocking.get())
                    // 休息指定时间
                    Thread.sleep(ConfigurationSetting.BLOCK_WAIT_TIME);

                // 将用户数据读取到队列当中

                readUserGroupDataInQueue();
                // 当读取完毕后解锁
                workComplete();

                // 如果结束, 则将全局记录的Reader数量减一并阻塞当前线程
                if (isEnd()) {
                    this.THREADS_COUNT.getAndDecrement();
                    logger.info("read all of userGroup: " + userGroupDir.getName());
                    blockAndResetState();
                }

                while (!blocking.compareAndSet(false, true)) ;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shutdownComplete();
    }

    public void blockAndResetState() throws InterruptedException {
        synchronized (this) {
            this.wait();
            for (String fileName : fileOffsetRecorder.keySet()) {
                fileOffsetRecorder.put(fileName, 0L);
            }
            finishFileCount = 0;
            end = false;
        }
    }

    @Override
    public void shutdownComplete() {
        super.shutdownComplete();
    }
}
