package com.tns.espapp.database;

/**
 * Created by TNS on 12/22/2016.
 */

public class TaxiFormData {

    private int id;
    private int keyid;
    private String selectdate;
    private String  projecttype;
    private String   formno;
    private String  vechicleno;
    private String  startkm;
    private String  startkm_image;
    private String endkm;
    private String endkmimage;
    private int flag;

    public TaxiFormData() {
    }

    public TaxiFormData(int keyids,String selectdate,String formno,String projecttype,String vechicleno, String startkm, String startkm_image, String endkm, String endkmimage,int flag) {
        this. keyid =keyids;
        this.selectdate = selectdate;
        this.formno = formno;
        this.projecttype = projecttype;
        this.vechicleno = vechicleno;
        this.startkm = startkm;
        this.startkm_image = startkm_image;
        this.endkm = endkm;
        this.endkmimage = endkmimage;
        this.flag = flag;
    }

/*
    public TaxiFormData(String selectdate, String projecttype, String startkm, String startkm_image, String endkm, String endkmimage, boolean flag) {

        this.selectdate = selectdate;
        this.projecttype = projecttype;
        this.startkm = startkm;
        this.startkm_image = startkm_image;
        this.endkm = endkm;
        this.endkmimage = endkmimage;
        this.flag = flag;
    }
*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKeyid() {
        return keyid;
    }

    public void setKeyid(int keyid) {
        this.keyid = keyid;
    }

    public String getSelectdate() {
        return selectdate;
    }

    public void setSelectdate(String selectdate) {
        this.selectdate = selectdate;
    }

    public String getFormno() {
        return formno;
    }

    public void setFormno(String formno) {
        this.formno = formno;
    }


    public String getProjecttype() {
        return projecttype;
    }

    public void setProjecttype(String projecttype) {
        this.projecttype = projecttype;
    }


    public String getVechicleno() {
        return vechicleno;
    }

    public void setVechicleno(String vechicleno) {
        this.vechicleno = vechicleno;
    }

    public String getStartkm() {
        return startkm;
    }

    public void setStartkm(String startkm) {
        this.startkm = startkm;
    }

    public String getStartkm_image() {
        return startkm_image;
    }

    public void setStartkm_image(String startkm_image) {
        this.startkm_image = startkm_image;
    }

    public String getEndkm() {
        return endkm;
    }

    public void setEndkm(String endkm) {
        this.endkm = endkm;
    }

    public String getEndkmimage() {
        return endkmimage;
    }

    public void setEndkmimage(String endkmimage) {
        this.endkmimage = endkmimage;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
