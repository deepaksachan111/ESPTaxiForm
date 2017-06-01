package com.tns.espapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tns.espapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;

public class RouteMapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    {
        TextView tv_first;
        private GoogleMap mMap;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        Marker mCurrLocationMarker;
        LocationRequest mLocationRequest;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_route_maps);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);



            ImageView iv_back =(ImageView)findViewById(R.id.iv_backss);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });



        }

        @Override
        protected void onStart() {
            super.onStart();


        getLocation();


        }

        @Override
        protected void onStop() {
            super.onStop();
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        }

        private void getLocation() {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();


            mGoogleApiClient.connect();
            Log.d("RoutemapActivity", "Location update resumed .....................");

            //  return loc;
        }
        protected void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d("RouteMapActivity", "Location update stopped .......................");

        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);



        }


    @Override
    public void onConnected(Bundle bundle) {

       startLocationUpdates();


    }

        protected void startLocationUpdates() {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.d("Routemapactivity", "Location update started ..............: ");
        }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
       mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        tv_first =(TextView)findViewById(R.id.tv_first);

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                Log.v("getLocation",strReturnedAddress.toString());
                tv_first.setText(strReturnedAddress.toString());

            }
            else{
                tv_first.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // tv_first.setText("Canont get Address!");
        }




    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }




}

class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }
}

class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                googleMap.addMarker(markerOptions);
            }
        }
    }

    public class Places {

        public List<HashMap<String, String>> parse(JSONObject jsonObject) {
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jsonArray);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
            int placesCount = jsonArray.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> placeMap = null;

            for (int i = 0; i < placesCount; i++) {
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placesList.add(placeMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
            HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }
                if (!googlePlaceJson.isNull("vicinity")) {
                    vicinity = googlePlaceJson.getString("vicinity");
                }
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = googlePlaceJson.getString("reference");
                googlePlaceMap.put("place_name", placeName);
                googlePlaceMap.put("vicinity", vicinity);
                googlePlaceMap.put("lat", latitude);
                googlePlaceMap.put("lng", longitude);
                googlePlaceMap.put("reference", reference);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return googlePlaceMap;
        }
    }


}

class Http {

    public String read(String httpUrl) throws IOException {
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d("Exception - reading Http url", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }
}

    /*private static final String TAG = "LocationActivity";

    Location mCurrentLocation;
    String mLastUpdateTime;
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 5000;

    private ArrayList<LatLng> arrayPoints = null;

    private TextView   tv_first;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_maps);

        arrayPoints = new ArrayList<LatLng>();
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available


        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
           fm.getMapAsync(this);

  *//*      View plusMinusButton = fm.getView().findViewById(1);
        View locationButton = fm.getView().findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams)     locationButton.getLayoutParams();
        RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams)     plusMinusButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_LEFT,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        llp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        llp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);


        ImageView iv_back =(ImageView)findViewById(R.id.iv_backss);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
*//*

        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider,1000,0, (android.location.LocationListener) this);

        final EditText editText =(EditText)findViewById(R.id.edt_search_loc);
        Button btn_find =(Button)findViewById(R.id.btn_find);
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editText.getText().toString().toLowerCase();
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&types=" + type);
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + getString(R.string.google_maps_key));

                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = googleMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
            }
        });

        tv_first =(TextView)findViewById(R.id.tv_first);


    }


    @Override
    public void onMapReady(GoogleMap googleMaps) {
        googleMap = googleMaps;
        // Add a marker in Sydney and move the camera
        //LatLng TutorialsPoint = new LatLng(21, 57);
       // googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Tutorialspoint.com"));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
    }






    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mCurrentLocation.getSpeed();
        mCurrentLocation.distanceTo(location);
        addMarker();


    }



    private void addMarker() {
        MarkerOptions options = new MarkerOptions();

      *//*  IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());*//*


        options.draggable(true);
        options.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);

         googleMap.addMarker(options);
      //  Marker mapMarker = googleMap.addMarker(options);

        long atTime = mCurrentLocation.getTime();
       // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
     //   mapMarker.setTitle(mLastUpdateTime);
        Log.d(TAG, "Marker added.............................");

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        arrayPoints.add(currentLatLng);
        polylineOptions.addAll(arrayPoints);
        googleMap.addPolyline(polylineOptions);


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));

      //  googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        Log.d(TAG, "Zoom done.............................");


        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                tv_first.setText(strReturnedAddress.toString());

            }
            else{
                tv_first.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // tv_first.setText("Canont get Address!");
        }


    }


    class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        GoogleMap googleMap;

        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                googleMap = (GoogleMap) inputObj[0];
                String googlePlacesUrl = (String) inputObj[1];
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
            Object[] toPass = new Object[2];
            toPass[0] = googleMap;
            toPass[1] = result;
            placesDisplayTask.execute(toPass);
        }
    }

    class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

        JSONObject googlePlacesJson;
        GoogleMap googleMap;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

            List<HashMap<String, String>> googlePlacesList = null;
            Places placeJsonParser = new Places();

            try {
                googleMap = (GoogleMap) inputObj[0];
                googlePlacesJson = new JSONObject((String) inputObj[1]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            googleMap.clear();
            if(list != null) {
                for (int i = 0; i < list.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    HashMap<String, String> googlePlace = list.get(i);
                    double lat = Double.parseDouble(googlePlace.get("lat"));
                    double lng = Double.parseDouble(googlePlace.get("lng"));
                    String placeName = googlePlace.get("place_name");
                    String vicinity = googlePlace.get("vicinity");
                    LatLng latLng = new LatLng(lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    googleMap.addMarker(markerOptions);
                }
            }
        }
    }

    public class Places {

        public List<HashMap<String, String>> parse(JSONObject jsonObject) {
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jsonArray);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
            int placesCount = jsonArray.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> placeMap = null;

            for (int i = 0; i < placesCount; i++) {
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placesList.add(placeMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
            HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }
                if (!googlePlaceJson.isNull("vicinity")) {
                    vicinity = googlePlaceJson.getString("vicinity");
                }
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = googlePlaceJson.getString("reference");
                googlePlaceMap.put("place_name", placeName);
                googlePlaceMap.put("vicinity", vicinity);
                googlePlaceMap.put("lat", latitude);
                googlePlaceMap.put("lng", longitude);
                googlePlaceMap.put("reference", reference);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return googlePlaceMap;
        }
    }

     class Http {

        public String read(String httpUrl) throws IOException {
            String httpData = "";
            InputStream inputStream = null;
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(httpUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                httpData = stringBuffer.toString();
                bufferedReader.close();
            } catch (Exception e) {
                Log.d("Exception - reading Http url", e.toString());
            } finally {
                inputStream.close();
                httpURLConnection.disconnect();
            }
            return httpData;
        }
    }




}




*/