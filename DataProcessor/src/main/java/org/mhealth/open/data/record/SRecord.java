package org.mhealth.open.data.record;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.NANOS;

public class SRecord implements Delayed{
    protected String userId = null;
    protected Instant date = null;
    protected String code = null;
    protected String rcode = null;
    protected Instant start = null;
    protected Instant stop = null;
    protected String encounter = null;
    protected String description = null;
    protected String reasondescription = null;
    protected long timestamp;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SRecord(){
        timestamp = System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        Instant sdate = null;
        if (this.date!=null)
            sdate = this.date;
        if (this.start!=null)
            sdate = this.start;
//        return unit.convert(ConfigurationSetting.SYNTHEA_CLOCK.instant().until(sdate, HOURS) , TimeUnit.HOURS);
        return ConfigurationSetting.SYNTHEA_CLOCK.instant().until(sdate, HOURS);
    }



    @Override
    public int compareTo(Delayed o) {
        // 比较延迟时间，值越大优先级越低
        return (int) (this.getDelay(TimeUnit.HOURS) - o.getDelay(TimeUnit.HOURS));
    }

    @Override
    public String toString (){
        return userId+"\n"+date+"\n"+start+" --- "+stop+"\n"+encounter+"\n"+description+"\n"+reasondescription;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getRcode() {
        return rcode;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getStop() {
        return stop;
    }

    public String getEncounter() {
        return encounter;
    }

    public String getDescription() {
        return description;
    }

    public String getReasondescription() {
        return reasondescription;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}
