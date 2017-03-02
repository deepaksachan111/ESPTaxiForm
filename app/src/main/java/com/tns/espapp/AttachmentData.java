package com.tns.espapp;

import java.io.File;

/**
 * Created by TNS on 08-Feb-17.
 */

public class AttachmentData {

    private int incriID;
    private String refNo;
    private String attachshow;
    private String attachsend;
   // private int fnid;
    private int flag;

    public AttachmentData() {
    }

    public AttachmentData(String attachshow, String attachsend) {
        this.attachshow = attachshow;
        this.attachsend = attachsend;
    }

    public AttachmentData(String ref,String attachshow, String attachsend, int flag) {
        this.refNo= ref;
        this.attachshow = attachshow;
        this.attachsend = attachsend;
        this.flag = flag;
    }

    public int getIncriID() {
        return incriID;
    }

    public void setIncriID(int incriID) {
        this.incriID = incriID;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getAttachshow() {
        return attachshow;
    }

    public void setAttachshow(String attachshow) {
        this.attachshow = attachshow;
    }

    public String getAttachsend() {
        return attachsend;
    }

    public void setAttachsend(String attachsend) {
        this.attachsend = attachsend;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


}
