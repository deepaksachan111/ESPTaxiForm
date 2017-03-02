package com.tns.espapp;

import java.io.File;

/**
 * Created by TNS on 08-Feb-17.
 */

public class CaptureData {
    private int incri_id;
    private  int fnID;
    private String refNo;
    private String captureImageshow;
    private String captureFilesend;
    private  int flag;

    public CaptureData() {
    }

    public CaptureData(String captureImageshowshow, String captureFilesend) {
        this.captureImageshow = captureImageshowshow;
        this.captureFilesend = captureFilesend;
    }

    public CaptureData(String refNo,String captureImageshow,String captureFilesend, int flag) {
        this.refNo = refNo;
        this.captureFilesend = captureFilesend;
        this.captureImageshow = captureImageshow;
        this.flag = flag;
    }

    public int getIncri_id() {
        return incri_id;
    }

    public void setIncri_id(int incri_id) {
        this.incri_id = incri_id;
    }

    public int getFnID() {
        return fnID;
    }

    public void setFnID(int fnID) {
        this.fnID = fnID;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getCaptureImageshow() {
        return captureImageshow;
    }

    public void setCaptureImageshow(String captureImageshow) {
        this.captureImageshow = captureImageshow;
    }

    public String getCaptureFilesend() {
        return captureFilesend;
    }

    public void setCaptureFilesend(String captureFilesend) {
        this.captureFilesend = captureFilesend;
    }
}
