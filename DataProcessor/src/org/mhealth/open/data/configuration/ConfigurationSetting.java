package org.mhealth.open.data.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mhealth.open.data.reader.MDataReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by dujijun on 2017/10/5.
 * <p>
 * 这个类之后会被改装成通过配置文件读入
 */
public class ConfigurationSetting {
    // 数据导入的路径
    public static final String dataRootPath;

    // 读取的时间间隔, 今后不使用指定间隔读取数据方法
    @Deprecated
    public static final long readingIntervalMillis;

    // 队列最大长度
    public static final int maxQueueSize;

    // 数据读取器的类
    public static final Class<? extends MDataReader> readerClass;

    // 包装所有和度量相关配置项
    public static final Map<String, MeasureConfiguration> measures = new HashMap<>();

    static {
        // 读入properties
        ClassLoader classLoader = ConfigurationSetting.class.getClassLoader();
        InputStream resource_in = classLoader.getResourceAsStream("conf.properties");
        Properties prop = new Properties();

        // 读入相应的数据

        String tmpDataRootPath = null;
        long tmpReadingIntervalMillis = 0l;
        int tmpMaxQueueSize = 0;
        Class tmpReaderClass = null;
        try {
            prop.load(resource_in);
            tmpDataRootPath = prop.getProperty("dataRootPath");
            tmpReadingIntervalMillis = Long.valueOf(prop.getProperty("readingIntervalMillis"));
            tmpMaxQueueSize = Integer.valueOf(prop.getProperty("maxQueueSize"));
            tmpReaderClass = Class.forName(prop.getProperty("readerClassName"));
            // 这里开始读入measure相关配置项
            String[] measureNames = prop.getProperty("measureNames").split(",");
            for(String name: measureNames){
                int readingFrequency = Integer.valueOf(prop.getProperty(name + ".readingFrequency"));
                float queueImportThreshold = Float.valueOf(prop.getProperty(name + ".queueImportThreshold"));
                measures.put(name, new MeasureConfiguration(name, readingFrequency, queueImportThreshold));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataRootPath = tmpDataRootPath;
        readingIntervalMillis = tmpReadingIntervalMillis;
        maxQueueSize = tmpMaxQueueSize;
        readerClass = tmpReaderClass;
    }

    public static Map<String, Queue> getSimpleContainer() {
        Map<String, Queue> queueMaps = new HashMap<>();
        Set<String> measureNames = measures.keySet();
        measureNames.forEach(name ->
            queueMaps.put(name,
                    new PriorityBlockingQueue<String>(maxQueueSize, ConfigurationSetting::compareRecord))
        );
        return queueMaps;
    }

    private static int compareRecord(String record1, String record2) {

        JSONObject jsonRecord1 = JSON.parseObject(record1);
        JSONObject jsonRecord2 = JSON.parseObject(record2);
        Date date1 = jsonRecord1
                .getJSONObject("body")
                .getJSONObject("effective_time_frame")
                .getDate("date_time");

        Date date2 = jsonRecord2
                .getJSONObject("body")
                .getJSONObject("effective_time_frame")
                .getDate("date_time");
        return -date1.compareTo(date2);

    }

}
