package com.tns.espapp.database;

/**
 * Created by TNS on 13-Feb-17.
 */

public class FeedbackRecordData {

    private  int fEEDBACK_RECORD_INCRIID ;
    private   String fEEDBACK_RECORD_UNITNAME;
    private   String fEEDBACK_RECORD_REFERENCENO;
    private   String fEEDBACK_RECORD_DATE ;
    private   String fEEDBACK_RECORD_BREIF ;
    private   int fEEDBACK_RECORD_FLAG ;
    private String fEED_LAT;
    private String fEED_LONG;

    public FeedbackRecordData() {
    }

    public FeedbackRecordData(String fEEDBACK_RECORD_UNITNAME, String fEEDBACK_RECORD_REFERENCENO, String fEEDBACK_RECORD_DATE, String fEEDBACK_RECORD_BREIF, int fEEDBACK_RECORD_FLAG,String lat,String log) {
        this.fEEDBACK_RECORD_UNITNAME = fEEDBACK_RECORD_UNITNAME;
        this.fEEDBACK_RECORD_REFERENCENO = fEEDBACK_RECORD_REFERENCENO;
        this.fEEDBACK_RECORD_DATE = fEEDBACK_RECORD_DATE;
        this.fEEDBACK_RECORD_BREIF = fEEDBACK_RECORD_BREIF;
        this.fEEDBACK_RECORD_FLAG = fEEDBACK_RECORD_FLAG;
         this. fEED_LAT=lat;
        this.fEED_LONG = log;
    }

    public int getfEEDBACK_RECORD_INCRIID() {
        return fEEDBACK_RECORD_INCRIID;
    }

    public void setfEEDBACK_RECORD_INCRIID(int fEEDBACK_RECORD_INCRIID) {
        this.fEEDBACK_RECORD_INCRIID = fEEDBACK_RECORD_INCRIID;
    }

    public String getfEEDBACK_RECORD_UNITNAME() {
        return fEEDBACK_RECORD_UNITNAME;
    }

    public void setfEEDBACK_RECORD_UNITNAME(String fEEDBACK_RECORD_UNITNAME) {
        this.fEEDBACK_RECORD_UNITNAME = fEEDBACK_RECORD_UNITNAME;
    }

    public String getfEEDBACK_RECORD_REFERENCENO() {
        return fEEDBACK_RECORD_REFERENCENO;
    }

    public void setfEEDBACK_RECORD_REFERENCENO(String fEEDBACK_RECORD_REFERENCENO) {
        this.fEEDBACK_RECORD_REFERENCENO = fEEDBACK_RECORD_REFERENCENO;
    }

    public String getfEEDBACK_RECORD_DATE() {
        return fEEDBACK_RECORD_DATE;
    }

    public void setfEEDBACK_RECORD_DATE(String fEEDBACK_RECORD_DATE) {
        this.fEEDBACK_RECORD_DATE = fEEDBACK_RECORD_DATE;
    }

    public String getfEEDBACK_RECORD_BREIF() {
        return fEEDBACK_RECORD_BREIF;
    }

    public void setfEEDBACK_RECORD_BREIF(String fEEDBACK_RECORD_BREIF) {
        this.fEEDBACK_RECORD_BREIF = fEEDBACK_RECORD_BREIF;
    }

    public int getfEEDBACK_RECORD_FLAG() {
        return fEEDBACK_RECORD_FLAG;
    }

    public void setfEEDBACK_RECORD_FLAG(int fEEDBACK_RECORD_FLAG) {
        this.fEEDBACK_RECORD_FLAG = fEEDBACK_RECORD_FLAG;
    }

    public String getfEED_LAT() {
        return fEED_LAT;
    }

    public void setfEED_LAT(String fEED_LAT) {
        this.fEED_LAT = fEED_LAT;
    }

    public String getfEED_LONG() {
        return fEED_LONG;
    }

    public void setfEED_LONG(String fEED_LONG) {
        this.fEED_LONG = fEED_LONG;
    }
}
