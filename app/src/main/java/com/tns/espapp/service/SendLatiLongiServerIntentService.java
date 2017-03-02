package com.tns.espapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;


import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.LatLongData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendLatiLongiServerIntentService extends IntentService {
    private static final ExecutorService pool = Executors.newSingleThreadExecutor();
    private String empid;
    private DatabaseHandler db;

    public SendLatiLongiServerIntentService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db = new DatabaseHandler(getApplicationContext());
        SharedPreferences sharedPreferences_setid = getApplicationContext().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");
        while (true) {
            List<LatLongData> latLongDataList = db.getAllLatLongORDerBy();
            int size = latLongDataList.size();
            if (size > 0) {
                for (LatLongData latLongData : latLongDataList) {
                   int id=  latLongData.getId();

                    if(latLongData.getLatlong_flag()== 0) {
                        String result = HTTPPostRequestMethod.postMethodforESP(AppConstraint.TAXITRACKROOT
                                , JsonParameterTaxiTrack(latLongData));
                        getResult(result, latLongData);

                    }
                }
            } else {
                break;
            }
        }
    }

    private void getResult(String s, LatLongData latLongData) {

        try {
            JSONArray   jsonArray = new JSONArray(s);
       
        for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("ID");
                    String status = jsonObject.getString("status");
            if (status.equals("1")) {
                latLongData.setLatlong_flag(1);
                db.updateLatLong(latLongData.getId(),latLongData.getFormno(),latLongData.getDate(),latLongData.getLat(),latLongData.getLongi(),latLongData.getLatlong_flag());
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject JsonParameterTaxiTrack(LatLongData latLongData) {
        String getDate_latlong = "";
        JSONObject jsonObject = new JSONObject();

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");

        try {

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");

            Date dtt = df.parse(latLongData.getDate());
            Date ds = new Date(dtt.getTime());
             getDate_latlong= dateFormat2.format(ds);
            System.out.println(getDate_latlong);

        } catch (ParseException e) {
            e.printStackTrace();
        }



        try {
            JSONArray jsonArrayParameter = new JSONArray();
            jsonArrayParameter.put(latLongData.getFormno());
            jsonArrayParameter.put(empid);
            jsonArrayParameter.put(latLongData.getLat());
            jsonArrayParameter.put(latLongData.getLongi());
            jsonArrayParameter.put(getDate_latlong);
            jsonArrayParameter.put(latLongData.getLatlong_flag());
            jsonArrayParameter.put("0");
            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_Taxi_Lat_Log");
            jsonObject.put("ParameterList", jsonArrayParameter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
