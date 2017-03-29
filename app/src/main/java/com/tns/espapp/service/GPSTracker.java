package com.tns.espapp.service;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.LocataionData;
import com.tns.espapp.NetworkConnectionchecker;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GPSTracker extends Service implements LocationListener {
    //  private final Context mContext;
    public static Handler handler = new Handler();
    public static final Bus BUS = new Bus();

    public static boolean isRunning = false;
    private Timer timer;

    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;

    boolean checkInternet;

    Location loc;
    double latitude;
    double longitude;
    Intent intent;
    public static final String BROADCAST_ACTION = "com.tns.espapp";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    String provider;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 2*60;
    protected LocationManager locationManager;
    private String form_no;
    private String empid;
    private String getdate;
    private String getDate_latlong;
    private String lats, longi;

    private  double diste;

    int flag = 1;
    DatabaseHandler db;

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
        intent = new Intent(BROADCAST_ACTION);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        form_no = intent.getStringExtra("formno");
        getdate = intent.getStringExtra("getdate");
        empid = intent.getStringExtra("empid");


        isRunning = true;
        // BUS.register(this);
        timer = new Timer();       // location.
        getLocation();
        Log.v("Service ", "ON start command is start");

        return START_STICKY;

    }

    private void getLocation() {

        try {

             locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!checkGPS && !checkNetwork) {
                Toast.makeText(getApplicationContext(), "No Service Provider Available", Toast.LENGTH_SHORT).show();
            }
            if (checkGPS || checkNetwork) {
                this.canGetLocation = true;
                // Getting LocationManager object from System Service LOCATION_SERVICE
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

                // Getting the name of the best provider
                provider = locationManager.getBestProvider(criteria, true);

                // Getting Current Location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                  //  return null;
                }
               // loc = locationManager.getLastKnownLocation(provider);


                if (loc != null) {

                    Toast.makeText(getApplicationContext(), "Service Provider Available", Toast.LENGTH_SHORT).show();
                   // onLocationChanged(loc);
                }

                locationManager.requestLocationUpdates(provider, 40000, 0, this);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

      //  return loc;
    }



    public void stopUsingGPS() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(GPSTracker.this);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onLocationChanged(Location location) {


        latitude = round(location.getLatitude(),6);
        longitude = round(location.getLongitude(),6);

        lats = String.format("%.6f", latitude) ;
        longi =  String.format("%.6f", longitude);
        //UpdateWithNewLocation();

        NetworkConnectionchecker connectionchecker = new NetworkConnectionchecker(getApplicationContext());
        checkInternet = connectionchecker.isConnectingToInternet();

       /*

        LocataionData locataionData =new LocataionData(latitude,longitude,checkInternet);
        // postevent(locataionData);
        intent.putExtra("EXTRA", (Serializable) locataionData);
        sendBroadcast(intent);

        */


        startService(new Intent(getApplication(), SendLatiLongiServerIntentService.class));
        List<LatLongData> latLongDataList = db.getLastLatLong(form_no);




        if(latLongDataList.size() > 0)

        {

            diste = 0.0000000;
            double d1_lat= Double.parseDouble(latLongDataList.get(0).getLat());
            double d1_long = Double.parseDouble(latLongDataList.get(0).getLongi());


            Log.v("Distance By GPS",diste+""+d1_lat+","+d1_long);
            diste=   distenc2(d1_lat,d1_long, latitude,longitude);

           // Toast.makeText(getApplicationContext(),diste+"",4000).show();

           /* for(LatLongData latLongData : latLongDataList){

                double d1_lat = Double.parseDouble(latLongData.getLat());
                double d1_long = Double.parseDouble(latLongData.getLongi());
                diste =Double.parseDouble(latLongData.getTotaldis());

                Log.v("Distance By GPS",diste+"");
                diste=   diste +distenc2(d1_lat,d1_long, latitude,longitude);
                Log.v("Dist By GPS Value Add",diste+"");

            }*/

        }


        if (checkInternet && latitude != 0.00) {
            flag = 0;

            db.addTaxiformLatLong(new LatLongData(form_no, getdate, lats, longi, flag, String.format("%.3f", diste)));
           // new getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);


        } else {

            flag = 0;
            db.addTaxiformLatLong(new LatLongData(form_no, getdate, lats, longi, flag,String.format("%.3f", diste)));

        }



    /*

     if (latitude != 0.00) {
           new getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);
        }

    */
        Log.v(latitude + "", longitude + "");




    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }





    private void UpdateWithNewLocation() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                // latitude = loc.getLatitude();
                // longitude = loc.getLongitude();

               /* DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                System.out.println(dateFormat.format(cal.getTime()));
                final String getDate = dateFormat.format(cal.getTime());*/

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                      /*  NetworkConnectionchecker connectionchecker = new NetworkConnectionchecker(getApplicationContext());
                        checkInternet = connectionchecker.isConnectingToInternet();

                      LocataionData locataionData =new LocataionData(latitude,longitude,checkInternet);
                     //   postevent(locataionData);
                        intent.putExtra("EXTRA", (Serializable) locataionData);

                        sendBroadcast(intent);*/
                    }
                });

            }
        }, 0, 100000);  // 1 minute
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.removeUpdates(GPSTracker.this);


        isRunning = false;

        //BUS.unregister(this);
    }

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


      /*      jsonObject.put("ftTaxiFormNo", form_no);
            jsonObject.put("Empid", empid);
            jsonObject.put("ftLat", lats);
            jsonObject.put("ftLog", longi);
            jsonObject.put("fdCreatedDate", getDate);
            jsonObject.put("fbStatus", flag);
            jsonObject.put("fnTaxiFormId", "0");*/



            // jsonObject.put("spName","USP_Get_Attendance");
            jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }



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

   private double distenc2(double a, double b, double c, double d){


        double distance;
        Location locationA = new Location("point A");
        locationA.setLatitude(a);
        locationA.setLongitude(b);

        Location locationB = new Location("point B");
        locationB.setLatitude(c);
        locationB.setLongitude(d);

        // distance = locationA.distanceTo(locationB);   // in meters
        distance = locationA.distanceTo(locationB)/1000;
        Log.v("Distance", distance+"");
        return distance;

    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }





}