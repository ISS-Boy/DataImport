package org.mhealth.open.data.util;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/*
 * 自定义时钟，通过tickPerSecond配置速度
 */
public final class ClockService implements Serializable {
    private static final long serialVersionUID = 1L;

    // 缓冲时间,用于生产者读取数据到队列中
    private static final long CUSHION_TIME = 90L;
    private final Instant startDateTime;
    private final long initialTimestamp;
    private final int tickPerSecond;

    public ClockService(Instant startDateTime, int tickPerSecond) {
        this.startDateTime = startDateTime.minus(CUSHION_TIME*tickPerSecond, ChronoUnit.SECONDS);
        this.initialTimestamp = System.currentTimeMillis();
        this.tickPerSecond = tickPerSecond;

    }

    public long millis() {
        return Math.addExact(startDateTime.toEpochMilli(),
                Math.multiplyExact(Math.subtractExact(System.currentTimeMillis(),initialTimestamp),tickPerSecond));
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
        return Objects.hash(startDateTime,initialTimestamp,tickPerSecond);
    }

    @Override
    public String toString() {
        return "ClockService[ "+ startDateTime + "," +tickPerSecond + "]";
    }
}