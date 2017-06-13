package com.tns.espapp.fragment;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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
import com.tns.espapp.R;
import com.tns.espapp.RouteMapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteMapFragment extends Fragment  implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        {

        TextView tv_first;
        private GoogleMap mMap;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        Marker mCurrLocationMarker;
        LocationRequest mLocationRequest;

        double latitude = 0;
        double longitude = 0;
        private int PROXIMITY_RADIUS = 5000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_route_maps, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //  Add..............
        tv_first =(TextView)v.findViewById(R.id.tv_first);

        final EditText editText =(EditText)v.findViewById(R.id.edt_search_loc);
        Button btn_find =(Button)v.findViewById(R.id.btn_find);
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
                toPass[0] = mMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
            }
        });



        return v;
    }

            @Override
            public void onStart() {
                super.onStart();


                getLocation();


            }

            @Override
            public void onStop() {
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
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                Log.v("getdata",location.getLatitude()+","+ location.getLongitude()+","+location.getAccuracy()+","+location.distanceTo(location));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


                Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("Address:");
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
                    PlacesDisplayTask.Places placeJsonParser = new PlacesDisplayTask.Places();

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

}
