package org.mhealth.open.data.record;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Allergies extends SRecord{
    private Map<String,String> allergies = new HashMap<>();

    public Allergies(String[][] lines) {
        String[] basic = lines[0];
        try {
            this.start = dateFormat.parse(basic[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = basic[2];
        this.encounter = basic[3];
        for(int i = 0;i<lines.length;i++){
            String[] line = lines[i];
            //提取每行的第5，6列作为码和描述
            allergies.put(line[4],line[5]);
        }
    }

    public Allergies(ArrayList<String[]> lines) {
        String[] basic = lines.get(0);
        try {
            this.start = dateFormat.parse(basic[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userId = basic[2];
        this.encounter = basic[3];
        for(int i = 0;i<lines.size();i++){
            String[] line = lines.get(i);
            //提取每行的第5，6列作为码和描述
            allergies.put(line[4],line[5]);
        }
    }

    public Instant getStart() {
        return start;
    }

    public String getCode() { return code;}

    public String getRCode() { return rcode;}

    public String getUserId() {
        return userId;
    }

    public String getEncounter() {
        return encounter;
    }

    public Map<String, String> getAllergies() { return allergies; }

    public String toString() {
        return userId+"\n"+start+"\n"+encounter+"\n"+allergies.toString();
    }
}
