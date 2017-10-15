package org.mhealth.open.data.reader;

import com.alibaba.fastjson.JSON;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.time.Instant;
import java.time.LocalDateTime;
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
    private String msg;
    private Instant date;
    private String userId;
    private String measureName;
    private LocalDateTime timestamp;
    private boolean poisonFlag;

    public MRecord(String msg, String measureName) {
        this.msg = msg;
        this.measureName = measureName;
        date = JSON.parseObject(msg)
                .getJSONObject("body")
                .getJSONObject("effective_time_frame")
                .getDate("date_time")
                .toInstant();
        userId = JSON.parseObject(msg)
                .getJSONObject("header")
                .getString("user_id");
        timestamp = LocalDateTime.now();
    }

    public MRecord(boolean flag, Instant date) {
        this.poisonFlag = flag;
        this.date = date;
    }

    public Instant getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }

    public String getMeasureName() {
        return measureName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isPoisonPill() {
        return poisonFlag;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        return unit.convert(ConfigurationSetting.CLOCK.instant().until(this.date, NANOS)/100, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        // 比较延迟时间，值越大优先级越低
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DelayRecord[");
//        sb.append("msg:'").append(msg).append("'")
        sb.append("measure:").append(measureName)
                .append(", date:").append(date)
                .append(", user_id:").append(userId)
                .append(", timestamp:").append(timestamp)
                .append("]");
        return sb.toString();
    }


}
