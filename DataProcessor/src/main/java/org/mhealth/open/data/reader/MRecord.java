package org.mhealth.open.data.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * for DataImport
 *
 * @author just on 2017/10/8.
 */
public class MRecord implements Delayed {
    private Instant date;
    private String userId;
    private String measureName;
    private long timestamp;

    private Map<String, Map<String, String>> measures = new HashMap<>();
    private boolean poisonFlag;

    public MRecord(String msg, String measureName) {
        this.measureName = measureName;
        date = JSON.parseObject(msg)
                .getJSONObject("body")
                .getJSONObject("effective_time_frame")
                .getDate("date_time")
                .toInstant();
        userId = JSON.parseObject(msg)
                .getJSONObject("header")
                .getString("user_id");
        JSON.parseObject(msg)
                .getJSONObject("body")
                .forEach((key, values) -> {
                    if (!key.equals("effective_time_frame")) {
                        Map<String, String> value = new HashMap<>();
                        ((JSONObject) values).forEach((k, v) -> {
                            value.put(k, v.toString());
                        });
                        measures.put(key, value);
                    }
                });

        timestamp = System.currentTimeMillis();
    }

    public MRecord(boolean flag, Instant date) {
        this.poisonFlag = flag;
        this.date = date;
        this.timestamp = System.currentTimeMillis();
    }

    public Instant getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public String getMeasureName() {
        return measureName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPoisonPill() {
        return poisonFlag;
    }

    public Map<String, Map<String, String>> getMeasures() {
        return measures;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        return unit.convert(ConfigurationSetting.CLOCK.instant().until(this.date, NANOS) / 2, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        // 比较延迟时间，值越大优先级越低
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DelayRecord[");
        sb.append("measure:").append(measureName)
                .append(", date:").append(date)
                .append(", user_id:").append(userId)
                .append(", timestamp:").append(Instant.ofEpochMilli(timestamp))
                .append("]");
        return sb.toString();
    }


}
