package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;

public class Patients extends SRecord{
    private String name;
    private Instant birthdate;
    private Instant deathdate;
    private String gender;
    private String race;

    public Patients(String[] line) {
        this.userId = line[0];
        try {
            this.birthdate = dateFormat.parse(line[1]).toInstant();
            if(!line[2].equals("")) {
                this.deathdate = dateFormat.parse(line[2]).toInstant();
            }else deathdate = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.name = line[7]+line[8];
        this.race = line[12];
        this.gender = line[14];

    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Instant getBirthdate() {
        return birthdate;
    }

    public Instant getDathdate() { return deathdate; }

    public String getGender() {
        return gender;
    }

    public String getRace() {
        return race;
    }

    public String toString() {
        return userId+"\n"+name+"\n"+gender+"\n"+race+"\n"+birthdate+" --- "+deathdate;
    }
}
