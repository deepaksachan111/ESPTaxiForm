package com.tns.espapp.database;

import java.util.ArrayList;

/**
 * Created by TNS on 12/22/2016.
 */
public class Picture {




    private static ArrayList<Picture> pictureArrayList;
    // private static final String folder = Environment.getExternalStorageDirectory()+ File.separator+ "myapp" + File.separator;


    static {
        pictureArrayList = new ArrayList<Picture>();
        // pictureArrayList.add(new Picture("Deepak", "My Messanger", "From", folder +"deepak.jpg"));

    }

    public static ArrayList<Picture> getPictureArrayList() {
        return pictureArrayList;
    }





    private String bmp;
    private String course;
    private String Date;


    public Picture(String b, String n, String k) {

        course = b;
        Date = n;
        bmp = k;
    }

    public String getBmp() {
        return bmp;
    }

    public void setBmp(String bmp) {
        this.bmp = bmp;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}