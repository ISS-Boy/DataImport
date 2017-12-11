package org.mhealth.open.data.record;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.text.ParseException;
import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

public class Patients extends SRecord{
    private String name;
    private Instant birthdate;
    private Instant deathdate;
    private String gender;
    private String race;
    private String birthplace;
    private String address;

    public Patients(String[] line) {
        this.userId = line[0];
        try {
            this.birthdate = dateFormat.parse(line[1]).toInstant();
            if(!line[2].equals("")) {
                this.deathdate = dateFormat.parse(line[2]).toInstant();
            }else deathdate = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.name = line[7]+line[8];
        this.race = line[12];
        this.gender = line[14];
        this.birthplace = line[15];
        this.address = line[16];

    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Instant getBirthdate() {
        return birthdate;
    }

    public Instant getDeathdate() { return deathdate; }

    public String getGender() {
        return gender;
    }

    public String getRace() {
        return race;
    }

    public long getDelay(TimeUnit unit) {
        // 计算数据时间与"当前"时间的差值，以此作为延迟时间返回
        // 延迟时间为负数或零时被取出
        return Math.subtractExact(this.birthdate.toEpochMilli(),ConfigurationSetting.CLOCK.millis()) / (10 * ConfigurationSetting.SYNTHEA_TICK_PER_SECOND);
    }
    public int compareTo(Delayed o) {
        // 比较延迟时间，值越大优先级越低
        return (int) (this.getDelay(TimeUnit.HOURS) - o.getDelay(TimeUnit.HOURS));
    }

    public String toString() {
        return userId+"\n"+name+"\n"+gender+"\n"+race+"\n"+birthdate+" --- "+deathdate+"\n"+birthplace+"\n"+address;
    }
}
