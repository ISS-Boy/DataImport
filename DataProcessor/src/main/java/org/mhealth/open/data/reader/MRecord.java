package org.mhealth.open.data.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.avro.Measure;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * for DataImport
 *
 * @author just on 2017/10/8.
 */
public class MRecord implements Delayed {

    private MEvent event;
    private String measureName;


    @Deprecated
    private boolean poisonFlag;

    public MRecord(String msg, String measureName) {
        this.measureName = measureName;
        this.event = parse(msg);

    }

    private MEvent parse(String json) {
        // 通过加上时间偏移量,计算得出"当前"时间,从而实现复用
        long timeOffsetMillis = ConfigurationSetting.TRUNCATE_OFFSET_TIME +
                                ConfigurationSetting.DURATION * ConfigurationSetting.DATA_REPEAT_TIME;

        Map<String, Measure> measures = new HashMap<>();
        MEvent mEvent = new MEvent();
        JSONObject root = JSON.parseObject(json);
        JSONObject header = root.getJSONObject("header");
        JSONObject body = root.getJSONObject("body");

        mEvent.setUserId(header.getString("user_id"));

        body.forEach((key, values) -> {
            if (key.equals("effective_time_frame")) {
                if (((JSONObject) values).containsKey("date_time")) {
                    mEvent.setTimestamp(((JSONObject) values).getDate("date_time").getTime() + timeOffsetMillis);
                } else {
                    mEvent.setTimestamp(((JSONObject) values).getJSONObject("time_interval")
                                                .getDate("start_date_time").getTime() + timeOffsetMillis);
                    measures.put("duration", construct(((JSONObject) values)
                            .getJSONObject("time_interval").getJSONObject("duration")));
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


    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
//        return unit.convert(Math.subtractExact(this.event.getTimestamp(), ConfigurationSetting.CLOCK.millis())
//                            / ConfigurationSetting.CLOCK.getTickPerSecond(),
//                TimeUnit.MILLISECONDS);
        return unit.convert(Math.subtractExact(this.event.getTimestamp(), System.currentTimeMillis()), TimeUnit.MILLISECONDS);
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
                .append("]");
        return sb.toString();
    }


}
