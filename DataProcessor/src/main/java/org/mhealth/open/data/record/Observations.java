package org.mhealth.open.data.record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Observations extends SRecord{

    //用来存放vital signs的集合
    private Map<String,String> signs = new HashMap<String, String>();

    public Observations(String[][] lines ) {
        String[] basic = lines[0];
        this.userId = basic[1];
        try {
            this.date = dateFormat.parse(basic[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.encounter = basic[3];
        for(int i = 0;i<lines.length;i++){
            String[] line = lines[i];
            //提取每行的第5，6列作为描述和值
            signs.put(line[4],line[5]);
        }
    }

    public Map<String,String> getSigns(){
        return signs;
    }

    public String toString() {
        return userId+"\n"+date+"\n"+encounter+"\n"+signs.toString();
        }
}