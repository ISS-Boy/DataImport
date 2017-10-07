package org.mhealth.open.data.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by dujijun on 2017/10/5.
 * <p>
 * 这个类之后会被改装成通过配置文件读入
 */
public class ConfigurationSetting {
    public static final String dataRootPath = "/Users/dujijun/Documents/大数据相关/数据生成/data";
    public static final long readingIntervalMillis = 5 * 60 * 1000;
    public static final Map<String, Integer> readingFrequency = new HashMap<>();

    static {
        readingFrequency.put("blood-pressure", 5);
        readingFrequency.put("body-fat-percentage", 1);
        readingFrequency.put("body-weight", 1);
        readingFrequency.put("heart-rate", 5);
    }

    public static Map<String, Queue> getSimpleContainer() {
        Map<String, Queue> queueMaps = new HashMap<>();
        queueMaps.put("blood-pressure", new PriorityBlockingQueue<String>(1024, ConfigurationSetting::compareRecord));
        queueMaps.put("body-fat-percentage", new PriorityBlockingQueue<String>(1024, ConfigurationSetting::compareRecord));
        queueMaps.put("body-weight", new PriorityBlockingQueue<String>(1024, ConfigurationSetting::compareRecord));
        queueMaps.put("heart-rate", new PriorityBlockingQueue<String>(1024, ConfigurationSetting::compareRecord));
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
