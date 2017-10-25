package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class Encounters extends SRecord{
    private String eId;

    public Encounters(String[] line){
        this.eId = line[0];
        try {
            this.date = dateFormat.parse(line[1]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[2];
        this.code = line[3];
        this.description = line[4];
        this.rcode = line[5];
        this.reasondescription = line[6];
    }

    public String geteId() {
        return eId;
    }

    public Instant getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() { return code;}

    public String getRCode() { return rcode;}

    public String getDescription() {
        return description;
    }

    public String getResondescription() {
        return reasondescription;
    }
}
