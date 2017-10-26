package org.mhealth.open.data.record;

import java.text.SimpleDateFormat;
import java.time.Instant;

public class SRecord {
    protected String userId = null;
    protected Instant date = null;
    protected String code = null;
    protected String rcode = null;
    protected Instant start = null;
    protected Instant stop = null;
    protected String encounter = null;
    protected String description = null;
    protected String reasondescription = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String toString (){
        return userId+"\n"+date+"\n"+start+" --- "+stop+"\n"+encounter+"\n"+description+"\n"+reasondescription;
    }
}
