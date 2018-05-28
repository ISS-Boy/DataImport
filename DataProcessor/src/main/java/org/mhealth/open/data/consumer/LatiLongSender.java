package org.mhealth.open.data.consumer;

import org.mhealth.open.data.avro.LatitudeAndLongitude;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.util.ClockService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dujijun on 2017/12/13.
 */
public class LatiLongSender implements Runnable {
    private List<LatitudeAndLongitude> lls;

    public LatiLongSender(List<LatitudeAndLongitude> lls) {
        this.lls = lls;
    }

    public List<LatitudeAndLongitude> getLls() {
        return lls;
    }

    public void setLls(List<LatitudeAndLongitude> lls) {
        this.lls = lls;
    }

    @Override
    public void run() {
        LaLoProducer producer = new LaLoProducer();
        while (true) {

            for(LatitudeAndLongitude ll : lls){
                ll.nextRandomValue();
                ll.setTimestamp(Instant.now().truncatedTo(ChronoUnit.MINUTES).toEpochMilli());
                producer.produce2Dest(ll);
            }

            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
