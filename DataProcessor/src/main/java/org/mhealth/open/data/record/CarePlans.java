package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class CarePlans extends SRecord{

    private String id;

    public CarePlans(String[] line) {
        this.id = line[0];
        try {
            this.start = dateFormat.parse(line[1]).toInstant();
            if(!line[2].equals("")) {
                this.stop = dateFormat.parse(line[2]).toInstant();
            }else stop = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = line[3];
        this.encounter = line[4];
        this.code = line[5];
        this.description = line[6];
        this.rcode = line[7];
        this.reasondescription = line[8];
    }

    public String getId() { return id; }

    public Instant getStart() { return start; }

    public Instant getStop() { return stop; }

    public String getUserId() { return userId; }

    public String getEncounter() { return encounter; }

    public String getDescription() { return description; }

    public String getReasondescription() { return reasondescription; }

    public String getCode() { return code;}

    public String getRCode() { return rcode;}
}
