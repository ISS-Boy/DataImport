package org.mhealth.open.data.util;

import org.mhealth.open.data.configuration.ConfigurationSetting;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * 生产环境中使用System.currentTime直接来表示当前时间,该类将仅供测试使用
 * 自定义时钟，通过tickPerSecond配置速度
 */
@Deprecated
public final class ClockService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Instant startDateTime;
    private final long initialTimestamp;
    private final int tickPerSecond;

    public ClockService(Instant startDateTime, int tickPerSecond) {
        this(startDateTime,tickPerSecond,0);
    }
    public ClockService(Instant startDateTime,int tickPerSecond,int cushionTime){
        this.startDateTime = startDateTime.minus(cushionTime * tickPerSecond, ChronoUnit.SECONDS);
        this.initialTimestamp = System.currentTimeMillis();
        this.tickPerSecond = tickPerSecond;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public int getTickPerSecond() {
        return tickPerSecond;
    }


    public long millis() {
        return Math.addExact(startDateTime.toEpochMilli(),
                Math.multiplyExact(Math.subtractExact(System.currentTimeMillis(), initialTimestamp), tickPerSecond));
    }

    public Instant instant() {
        return Instant.ofEpochMilli(millis());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClockService) {
            ClockService other = (ClockService) obj;
            return startDateTime.equals(other.startDateTime) && initialTimestamp == other.initialTimestamp && tickPerSecond == other.tickPerSecond;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDateTime, initialTimestamp, tickPerSecond);
    }

    @Override
    public String toString() {
        return "ClockService[ " + startDateTime + "," + tickPerSecond + "]";
    }
}