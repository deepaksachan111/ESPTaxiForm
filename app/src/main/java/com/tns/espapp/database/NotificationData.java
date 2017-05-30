package com.tns.espapp.database;

/**
 * Created by TNS on 16-May-17.
 */

public class NotificationData {
    public  int noti_id;
    public  String noti_tittle;
    public  String noti_message;
    public  String noti_image;
    public int flag;


    public NotificationData() {
    }

    public NotificationData(String noti_tittle, String noti_message, String noti_image, int flag) {
        this.noti_tittle = noti_tittle;
        this.noti_message = noti_message;
        this.noti_image = noti_image;
        this.flag = flag;
    }

    public String getNoti_tittle() {
        return noti_tittle;
    }

    public void setNoti_tittle(String noti_tittle) {
        this.noti_tittle = noti_tittle;
    }

    public String getNoti_message() {
        return noti_message;
    }

    public void setNoti_message(String noti_message) {
        this.noti_message = noti_message;
    }

    public String getNoti_image() {
        return noti_image;
    }

    public void setNoti_image(String noti_image) {
        this.noti_image = noti_image;
    }

    public int getNoti_id() {
        return noti_id;
    }

    public void setNoti_id(int noti_id) {
        this.noti_id = noti_id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
