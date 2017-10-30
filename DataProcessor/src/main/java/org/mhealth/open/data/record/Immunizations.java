package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class Immunizations extends SRecord{

    public Immunizations(String[] line) {
        try {
            this.date = dateFormat.parse(line[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[1];
        this.encounter = line[2];
        this.code = line[3];
        this.description = line[4];
    }

    public Instant getDate() {
        return date;
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
