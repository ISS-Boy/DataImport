package org.mhealth.open.data.record;

import java.text.SimpleDateFormat;
import java.time.Instant;

public class SRecord {
    protected String userId;
    protected Instant date;
    protected String code;
    protected String rcode;
    protected Instant start;
    protected Instant stop;
    protected String encounter;
    protected String description;
    protected String reasondescription;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String toString (){
        return userId+"\n"+date+"\n"+start+" --- "+stop+"\n"+encounter+"\n"+description+"\n"+reasondescription;
    }
}
