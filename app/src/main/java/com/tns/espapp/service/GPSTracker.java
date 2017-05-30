package com.tns.espapp.service;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.LocataionData;
import com.tns.espapp.NMEAParse;
import com.tns.espapp.NetworkConnectionchecker;
import com.tns.espapp.R;
import com.tns.espapp.activity.HomeActivity;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.LatLongData;
import com.tns.espapp.fragment.TaxiFormFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GPSTracker extends Service implements com.google.android.gms.location.LocationListener,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener , GpsStatus.Listener {

    //  private final Context mContext;
    private static final String TAG = "gpstracker";

    public static boolean isRunning = false;


    boolean checkInternet;

    double latitude;
    double longitude;
    Intent intent;
    public static final String BROADCAST_ACTION = "com.tns.espapp";

    private   int INTERVAL = 0;


    private String form_no;
    private String empid;
    private String getdate;
    private String lats, longi;

    private String getTime;

    private double diste;
    boolean flag_notification;
    int flag = 0;
    DatabaseHandler db;

    SharedPreferences preference;
    private String speed;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private static final float MIN_ACCURACY = 25.0f;
    private Location mBestReading;

    LocationManager lm ;

  /*  public GPSTracker(Context mContext) {
      //  this.mContext = mContext;
        isRunning = true;
        BUS.register(this);
        timer = new Timer();
        getLocation();
    }*/


    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(this);

        preference = getApplicationContext().getSharedPreferences("SERVICE", Context.MODE_PRIVATE);
        form_no = preference.getString("formno", "");
        getdate = preference.getString("getdate", "");
        empid = preference.getString("empid", "");

        SharedPreferences sharedPreferences_setid =getApplicationContext().getSharedPreferences("ID", Context.MODE_PRIVATE);
        INTERVAL = sharedPreferences_setid.getInt("gpstimesec",0);


        intent = new Intent(BROADCAST_ACTION);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        isRunning = true;
        // BUS.register(this);
        // location.
        getLocation();
        Log.v("Service ", "ON start command is start");
        return START_STICKY;

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.addGpsStatusListener(this);
    }

    private void getLocation() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mGoogleApiClient.connect();
        Log.d(TAG, "Location update resumed .....................");

        //  return loc;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onLocationChanged(Location location) {





        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        getTime = time_formatter.format(location.getTime());

        double getspeed = 3.6 * location.getSpeed();
        speed = String.format("%.3f", getspeed);
        latitude = round(location.getLatitude(), 6);
        longitude = round(location.getLongitude(), 6);




        Log.v("LatLong", latitude + "," + longitude);

        lats = String.format("%.6f", latitude);
        longi = String.format("%.6f", longitude);

        //UpdateWithNewLocation();


        NetworkConnectionchecker connectionchecker = new NetworkConnectionchecker(getApplicationContext());
        checkInternet = connectionchecker.isConnectingToInternet();

        if (!flag_notification) {
            showNotification();
            flag_notification = true;
        }


        // postevent(locataionData);
        intent.putExtra("EXTRA", isRunning);
        sendBroadcast(intent);


        startService(new Intent(getApplication(), SendLatiLongiServerIntentService.class));
        List<LatLongData> latLongDataList = db.getLastLatLong(form_no);

        if (latLongDataList.size() > 0)

        {
            diste = 0.0000000;
            double d1_lat = Double.parseDouble(latLongDataList.get(0).getLat());
            double d1_long = Double.parseDouble(latLongDataList.get(0).getLongi());

            Log.d("Distance By GPS", diste + "" + d1_lat + "," + d1_long);
            diste = distenc2(d1_lat, d1_long, latitude, longitude);


        }


        flag = 0;

/*
        if( location.getSpeed() >= 0.100) {
            db.addTaxiformLatLong(new LatLongData(form_no, getdate, lats, longi, flag, String.format("%.3f", diste), getTime, speed));
            Log.v(TAG, getacc + "," + MIN_ACCURACY);

        }else{

            Log.d(TAG,"speed"+ speed);
        }*/


        db.addTaxiformLatLong(new LatLongData(form_no, getdate, lats, longi, flag, String.format("%.3f", diste), getTime, speed));



    /*

     if (latitude != 0.00) {
           new getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);
        }

    */

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.commit();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        isRunning = false;

        //BUS.unregister(this);
    }

/*
    public void postevent(LocataionData s ){
        BUS.post(s);
    }

    private class getDataTrackTaxiAsnycTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  pd.setMessage("Loading");

            //  pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String s = HTTPPostRequestMethod.postMethodforESP(params[0],JsonParameterTaxiTrack());
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String re  = s;
            try {

                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0; i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String status = jsonObject.getString("status");
                    String id = jsonObject.getString("ID");
                    db.addTaxiformLatLong(new LatLongData(form_no, getdate, lats, longi, flag,String.valueOf(diste)));
                }
            } catch (JSONException e) {

                //  Toast.makeText(getActivity(),"Internet is not working",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }
    }


    private    JSONObject  JsonParameterTaxiTrack() {

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");
        try {

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
            Date dtt = df.parse(getdate);
            Date ds = new Date(dtt.getTime());
            getDate_latlong = dateFormat2.format(ds);
            System.out.println(getDate_latlong );

        } catch (ParseException e) {
            e.printStackTrace();
        }



        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArrayParameter = new JSONArray();
            jsonArrayParameter.put(form_no);
            jsonArrayParameter.put(empid);
            jsonArrayParameter.put(lats);
            jsonArrayParameter.put(longi);
            jsonArrayParameter.put(getDate_latlong);
            jsonArrayParameter.put(flag);
            jsonArrayParameter.put("0");
            jsonArrayParameter.put(diste);


            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_Taxi_Lat_Log");


      */
/*      jsonObject.put("ftTaxiFormNo", form_no);
            jsonObject.put("Empid", empid);
            jsonObject.put("ftLat", lats);
            jsonObject.put("ftLog", longi);
            jsonObject.put("fdCreatedDate", getDate);
            jsonObject.put("fbStatus", flag);
            jsonObject.put("fnTaxiFormId", "0");*//*




            // jsonObject.put("spName","USP_Get_Attendance");
            jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
*/


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double distenc2(double a, double b, double c, double d) {


        double distance;
        Location locationA = new Location("point A");
        locationA.setLatitude(a);
        locationA.setLongitude(b);

        Location locationB = new Location("point B");
        locationB.setLatitude(c);
        locationB.setLongitude(d);

        // distance = locationA.distanceTo(locationB);   // in meters
        distance = round(locationA.distanceTo(locationB) / 1000, 6);
        Log.v("Distance", distance + "");
        return distance;

    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        Notification myNotication;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(GPSTracker.this);


        builder.setAutoCancel(true);
        builder.setTicker("Telecom Network Solutions ");
        builder.setContentTitle("Taxi App");
        builder.setContentText("Start Travelling");
        builder.setSmallIcon(R.mipmap.taxi_icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Running Status");   //API level 16
        // builder.setNumber(1);
        builder.build();
        //builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        myNotication = builder.getNotification();
        manager.notify(11, myNotication);

         /*
                //API level 8
                Notification myNotification8 = new Notification(R.drawable.ic_launcher, "this is ticker text 8", System.currentTimeMillis());

                Intent intent2 = new Intent(MainActivity.this, SecActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 2, intent2, 0);
                myNotification8.setLatestEventInfo(getApplicationContext(), "API level 8", "this is api 8 msg", pendingIntent2);
                manager.notify(11, myNotification8);
                */


    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended ..............: ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed ..............: ");

    }

    @Override
    public void onGpsStatusChanged(int event) {




        GpsStatus gpsStatus = lm.getGpsStatus(null);

        if (gpsStatus != null) {
            Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            String lSatellites = null;
            int i = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "Satellite" + (i++) + ": "
                        + satellite.getPrn() + ","
                        + satellite.usedInFix() + ","
                        + satellite.getSnr() + ","
                        + satellite.getAzimuth() + ","
                        + satellite.getElevation() + "\n\n";

                Log.d("SATELLITE", lSatellites);
            }
        }


    }

}