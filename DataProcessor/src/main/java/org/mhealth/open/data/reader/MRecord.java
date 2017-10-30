package org.mhealth.open.data.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.avro.Measure;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.NANOS;

/**
 * for DataImport
 *
 * @author just on 2017/10/8.
 */
public class MRecord implements Delayed {

    private MEvent event;
    private String measureName;
    private long timestamp;

    private boolean poisonFlag;

    public MRecord(String msg, String measureName) {
        this.measureName = measureName;
        this.event = parse(msg);

        timestamp = System.currentTimeMillis();
    }

    public MRecord(boolean flag, Instant date) {
        this.poisonFlag = flag;
        event.setTimestamp(date.toEpochMilli());
        this.timestamp = System.currentTimeMillis();
    }
    private MEvent parse(String json) {

        Map<String, Measure> measures = new HashMap<>();
        MEvent mEvent = new MEvent();
        JSONObject root = JSON.parseObject(json);
        JSONObject header = root.getJSONObject("header");
        JSONObject body = root.getJSONObject("body");

        mEvent.setUserId(header.getString("user_id"));

        body.forEach((key, values) -> {
            if (key.equals("effective_time_frame")) {
                if (((JSONObject) values).containsKey("date_time")) {
                    mEvent.setTimestamp(((JSONObject) values).getDate("date_time").getTime());
                } else {
                    mEvent.setTimestamp(((JSONObject) values).getJSONObject("time_interval").getDate("start_date_time").getTime());
                    measures.put("duration", construct(((JSONObject) values).getJSONObject("time_interval").getJSONObject("duration")));
                }
            } else if (key.equals("sleep_duration")) {
                measures.putIfAbsent("duration", construct(values));
            } else {
                measures.put(key, construct(values));
            }
        });
        mEvent.setMeasures(measures);
        return mEvent;
    }
    private Measure construct(Object obj) {
        Measure measure = new Measure("", 0F);
        if (obj instanceof JSONObject) {
            measure = JSON.parseObject(obj.toString(), Measure.class);
        } else if (obj instanceof String) {
            measure.setUnit(obj.toString());
        } else {
            measure.setValue(Float.valueOf(obj.toString()));
        }
        return measure;
    }

    public MEvent getEvent() {
        return event;
    }

    public Instant getDate() {
        return Instant.ofEpochMilli(event.getTimestamp());
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


    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        return unit.convert(Math.subtractExact(this.event.getTimestamp(),ConfigurationSetting.CLOCK.millis()) / (2 * ConfigurationSetting.CLOCK.getTickPerSecond()),
                TimeUnit.MILLISECONDS);
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
                .append(", date:").append(getDate())
                .append(", user_id:").append(event.getUserId())
                .append(", timestamp:").append(Instant.ofEpochMilli(timestamp))
                .append("]");
        return sb.toString();
    }


}
