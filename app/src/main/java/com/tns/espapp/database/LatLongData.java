package com.tns.espapp.database;

import java.io.Serializable;

/**
 * Created by TNS on 1/3/2017.
 */

public class LatLongData {
    private   int id ;
    private String formno;
    private String date;
    private String lat;
    private String longi;
    private  int latlong_flag;

    public LatLongData() {
    }

    public LatLongData( String formno,String date, String lat, String longi, int latlong_flag) {

        this.formno = formno;
        this.date = date;
        this.lat = lat;
        this.longi = longi;
        this.latlong_flag = latlong_flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormno() {
        return formno;
    }

    public void setFormno(String formno) {
        this.formno = formno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public int getLatlong_flag() {
        return latlong_flag;
    }

    public void setLatlong_flag(int latlong_flag) {
        this.latlong_flag = latlong_flag;
    }
}
