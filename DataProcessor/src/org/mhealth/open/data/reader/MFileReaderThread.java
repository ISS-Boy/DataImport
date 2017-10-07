package org.mhealth.open.data.reader;

import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.exception.UnhandledQueueOperationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dujijun on 2017/10/5.
 */
public class MFileReaderThread extends AbstractMThread {

    private File userGroupDir;
    private Map<String, Queue> queueMaps;

    private Map<String, Long> fileOffsetRecorder = new HashMap<>();
    private boolean end = false;
    private int finishFileCount = 0;

    public MFileReaderThread(CountDownLatch startupLatch, CountDownLatch shutdownLatch, File userGroupDir, Map<String, Queue> queueMaps) {
        super(startupLatch, shutdownLatch);
        this.userGroupDir = userGroupDir;
        this.queueMaps = queueMaps;
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
                Queue measureQueue = Objects.requireNonNull(queueMaps.get(measureName), "队列未创建或文件名有误");

                // 开始正式读取文件
                // 获取文件对应的起始offset指针
                long startOffset;
                String offsetKey = userName + "-" + measureName;
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
                    int frequency = ConfigurationSetting.readingFrequency.get(measureName);
                    for (int i = 0; i < frequency && (record = raf.readLine()) != null; i++) {

                        // 这里应该有对应record的处理过程, 这里会有两种处理方式
                        // 1、直接当作字符串 ☑️
                        // 2、转换成对象来进行处理

                        if (!measureQueue.offer(record)) {
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

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 每次读一次文件，都应该判断是否全部读完
        end = finishFileCount == fileOffsetRecorder.keySet().size();

        // 测试一下
        System.out.printf("线程%s成功读取了一次用户组%s的数据文件\n", Thread.currentThread().getName(), userGroupDir.getName());

        // 休息指定时间
        Thread.sleep(ConfigurationSetting.readingIntervalMillis);

    }

    public boolean isEnd() {
        return end;
    }


    @Override
    public void run() {
        startupComplete();

        //每次都将用户组目录下的数据读入队列中
        try {
            while(!isEnd())
                readUserGroupDataInQueue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shutdownComplete();
    }
}
