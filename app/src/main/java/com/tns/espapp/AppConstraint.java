package com.tns.espapp;

import com.tns.espapp.service.SendLatiLongiServerIntentService;

import java.util.HashMap;

/**
 * Created by TNS on 12/21/2016.
 */

public interface AppConstraint {

   //  static final String APP_SERVER_URL = "http://192.168.2.4:9000/gcm/gcm.php?shareRegId=true";


     static final String ESP_LOGIN ="http://tnssofts.com/apiesp/api/Taxi/Login";
   // static final String ESP_LOGIN ="http://bkp-server";


    static final String FTP_HOST = "119.82.74.82";
    static final String FTP_USER = "tnssoft";
    static final String FTP_PASS = "@soft4321";

    static final String TAXIFORMURL ="http://tnssofts.com/apiesp/api/Taxi/Taxi";
    static final String TAXITRACKROOT  ="http://tnssofts.com/apiesp/api/Taxi/ExecuteJsonsp";
    static final String FEEDBACK_UNIT  ="http://tnssofts.com/apiesp/api/Taxi/ExecuteJson";
    static final String COMMONURL  ="http://tnssofts.com/apiesp/api/Taxi/ExecuteJsonsp";



    // Google Project Number
    static final String GOOGLE_PROJ_ID = "837715578074";
    // Message Key
    static final String MSG_KEY = "m";

    void hello();



}





/*

        S/No. 	FTP Folder	User Name	Password 	Permission
        1	TNSSOFT Folder	tnssoft	@soft4321	R/W

        1. FTP USE FOR OUT SIDE USER  Address is:- ftp://119.82.74.82
        2. FTP USE FOR OUT SIDE USER  Address is:- ftp://182.71.51.34
        3. FTP USE FOR INTERNAL USER  Address is:- ftp://192.168.1.6
*/
