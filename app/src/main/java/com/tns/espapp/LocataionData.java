package com.tns.espapp;

import java.io.Serializable;

/**
 * Created by TNS on 12/30/2016.
 */

public class LocataionData implements Serializable {

    private double latitute;
    private double longitute;
    private boolean status;

    public LocataionData() {
    }

    public LocataionData(double latitute, double longitute, boolean status) {
        this.latitute = latitute;
        this.longitute = longitute;
        this.status = status;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
