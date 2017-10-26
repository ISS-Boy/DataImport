package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class procedures extends SRecord{

    public procedures(String[] line) {
        try {
            this.date = dateFormat.parse(line[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[1];
        this.encounter = line[2];
        this.code = line[3];
        this.description = line[4];
        if(line.length>5) {
            this.rcode = line[5];
            this.reasondescription = line[6];
        }

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

    public String getCode() { return code;}

    public String getRCode() { return rcode;}

    public String getDescription() {
        return description;
    }

    public String getReasondescription() {
        return reasondescription;
    }
}
