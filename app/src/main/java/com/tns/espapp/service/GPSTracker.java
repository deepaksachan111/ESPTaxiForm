package com.tns.espapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.tns.espapp.LocataionData;
import com.tns.espapp.NetworkConnectionchecker;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 5;
    protected LocationManager locationManager;


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
        intent = new Intent(BROADCAST_ACTION);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        isRunning = true;
        // BUS.register(this);
        timer = new Timer();       // location.
        getLocation();
        Log.v("Service ", "ON start command is start");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    private Location getLocation() {

        try {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // getting GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(getApplicationContext(), "No Service Provider Available", Toast.LENGTH_SHORT).show();
            } else /*{
                this.canGetLocation = true;
                // First get location from Network Provider
                if (checkNetwork) {
                    Toast.makeText(getApplicationContext(), "Network", Toast.LENGTH_SHORT).show();

                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }

                        if (loc != null) {
                            UpdateWithNewLocation();
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }
                    catch(SecurityException e){
                        e.printStackTrace();
                    }
                }
            }*/
                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {
                    this.canGetLocation = true;
                    //Toast.makeText(getApplicationContext(),"GPS",Toast.LENGTH_SHORT).show();
                    if (loc == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");

                            if (locationManager != null) {
                                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (loc != null) {

                                    onLocationChanged(loc);

                              /*  latitude = loc.getLatitude();
                                longitude = loc.getLongitude();*/
                                }
                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();

                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
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

    @Override
    public void onLocationChanged(Location location) {

      latitude =  location.getLatitude();
        longitude = location.getLongitude();
        UpdateWithNewLocation();
        Log.v(latitude+"", latitude+"");

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

                        NetworkConnectionchecker connectionchecker = new NetworkConnectionchecker(getApplicationContext());
                        checkInternet = connectionchecker.isConnectingToInternet();

                      LocataionData locataionData =new LocataionData(latitude,longitude,checkInternet);
                     //   postevent(locataionData);
                        intent.putExtra("EXTRA", (Serializable) locataionData);
                        sendBroadcast(intent);

                    }
                });

            }
        }, 0, 500000);  // 5 minute
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

       // isRunning = false;

        //BUS.unregister(this);
    }

    public void postevent(LocataionData s ){
        BUS.post(s);
    }

}