package org.mhealth.open.data.record;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class Observations extends SRecord{

    //用来存放vital signs的集合
    private Map<String,Float> signs = new HashMap<>();

    public Observations(String[][] lines ) {
        String[] basic = lines[0];
        this.userId = basic[1];
        try {
            this.date = dateFormat.parse(basic[0]).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.encounter = basic[2];
        this.code = basic[3];
        for(int i = 0;i<lines.length;i++){
            String[] line = lines[i];
            //提取每行的第5，6列作为描述和值
            signs.put(line[4],Float.parseFloat(line[5]));
        }
    }

    public Map<String,Float> getSigns(){
        return signs;
    }

    public String toString() {
        return userId+"\n"+date+"\n"+encounter+"\n"+signs.toString();
        }
}