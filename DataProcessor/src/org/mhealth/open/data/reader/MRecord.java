package org.mhealth.open.data.reader;

import com.alibaba.fastjson.JSON;
import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * for DataImport
 *
 * @author just on 2017/10/8.
 */
public class MRecord implements Delayed{
    private String msg;
    private Instant date;
    private String userId;
    private String measureName;
    private long timestamp;
    
    public MRecord(String msg,String measureName) {
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
        timestamp = System.currentTimeMillis();
    }
    public MRecord(String poison,Instant date){
        this.measureName = poison;
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

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPoisonPill(){
        return measureName.equals("poisonPill");
    }
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(ConfigurationSetting.CLOCK.instant().until(this.date,SECONDS),TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int)(this.getDelay(TimeUnit.SECONDS)-o.getDelay(TimeUnit.SECONDS));
    }
//    @Override
//    public int compareTo(MRecord o) {
//        return (int)this.getDate().until(o.getDate(),SECONDS);
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DelayRecord[");
        sb.append("msg:'").append(msg).append("'")
                .append(", date:").append(date)
                .append(", user_id:").append(userId)
                .append(", measure_name:").append(measureName)
                .append("]");
        return sb.toString();
    }


}
