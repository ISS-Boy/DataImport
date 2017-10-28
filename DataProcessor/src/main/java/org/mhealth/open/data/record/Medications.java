package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class Medications extends SRecord{

    public Medications(String[] line) {
        try {
            this.start = dateFormat.parse(line[0]).toInstant();
            if(!line[1].equals("")) {
                this.stop= dateFormat.parse(line[1]).toInstant();
            }else stop = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[2];
        this.encounter = line[3];
        this.code = line[4];
        this.description = line[5];
        if(line.length>6) {
            this.rcode = line[6];
            this.reasondescription = line[7];
        }

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

    public String getCode() { return code;}

    public String getRCode() { return rcode;}

    public String getEncounter() {
        return encounter;
    }

    public String getDescription() {
        return description;
    }

    public String getReasondescription() {
        return reasondescription;
    }
}
