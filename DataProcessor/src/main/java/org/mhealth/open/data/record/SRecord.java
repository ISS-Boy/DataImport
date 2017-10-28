package org.mhealth.open.data.record;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

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
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        Instant sdate = null;
        if (this.date!=null)
            sdate = this.date;
        if (this.start!=null)
            sdate = this.start;
        return unit.convert(ConfigurationSetting.CLOCK.instant().until(sdate, HOURS) / (2*ConfigurationSetting.TICK_PER_SECOND), TimeUnit.HOURS);
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
}
