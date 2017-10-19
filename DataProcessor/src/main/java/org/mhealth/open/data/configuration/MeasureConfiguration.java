package org.mhealth.open.data.configuration;

/**
 * Created by dujijun on 2017/10/8.
 */
public class MeasureConfiguration {
    private String measureName;
    private int readingFrequency;
    private float queueImportThreshold;
    private int producerNums;

    public MeasureConfiguration(String measureName, int readingFrequency, float queueImportThreshold, int producerNums) {
        this.measureName = measureName;
        this.readingFrequency = readingFrequency;
        this.queueImportThreshold = queueImportThreshold;
        this.producerNums = producerNums;
    }

    public MeasureConfiguration(String measureName, int readingFrequency, float queueImportThreshold) {
        this(measureName,readingFrequency,queueImportThreshold,1);
    }

    public MeasureConfiguration() {
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public int getReadingFrequency() {
        return readingFrequency;
    }

    public void setReadingFrequency(int readingFrequency) {
        this.readingFrequency = readingFrequency;
    }

    public float getQueueImportThreshold() {
        return queueImportThreshold;
    }

    public void setQueueImportThreshold(float queueImportThreshold) {
        this.queueImportThreshold = queueImportThreshold;
    }

    public int getProducerNums() {
        return producerNums;
    }

    public void setProducerNums(int producerNums) {
        this.producerNums = producerNums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasureConfiguration that = (MeasureConfiguration) o;

        if (readingFrequency != that.readingFrequency) return false;
        if (Float.compare(that.queueImportThreshold, queueImportThreshold) != 0) return false;
        return measureName != null ? measureName.equals(that.measureName) : that.measureName == null;
    }

    @Override
    public int hashCode() {
        int result = measureName != null ? measureName.hashCode() : 0;
        result = 31 * result + readingFrequency;
        result = 31 * result + (queueImportThreshold != +0.0f ? Float.floatToIntBits(queueImportThreshold) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MeasureConfiguration{" +
                "measureName='" + measureName + '\'' +
                ", readingFrequency=" + readingFrequency +
                ", queueImportThreshold=" + queueImportThreshold +
                '}';
    }
}
