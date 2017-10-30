package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class Conditions extends SRecord{

    public Conditions(String[] line) {
        try {
            this.start = dateFormat.parse(line[0]).toInstant();
            if(!line[1].equals("")) {
                this.stop = dateFormat.parse(line[1]).toInstant();
            }else stop = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[2];
        this.encounter = line[3];
        this.code = line[4];
        this.description = line[5];
    }

    public Instant getStart() {
        return start;
    }

    public Instant getStop() {
        return stop;
    }

    public String getUserId() {
        return userId;
    }

    public String getEncounter() {
        return encounter;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() { return code;}

}
