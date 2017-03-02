package com.tns.espapp.fragment;



import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.LatLongData;
import com.tns.espapp.service.GPSTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LocationHistoryFragment extends Fragment {

    private ListView listview_locationhistory;
    private DatabaseHandler db;
    private EditText editsearch;
    private LatLongHistoryAdapter adapter;
    private ArrayList<LatLongData> latLongDataArrayList = new ArrayList<>();
    public LocationHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_locationhistory, container, false);
        listview_locationhistory=(ListView)v.findViewById(R.id.listview_locationhistory);
        db = new DatabaseHandler(getActivity());

        List<LatLongData> latLongDataList = db.getAllLatLong();
        int size = latLongDataList.size();
            if(size > 10000) {
                db.deleteSomeRow_LatLong();
            }
        if(size >0){
            for(LatLongData latLongData : latLongDataList){
                latLongDataArrayList.add(latLongData);
            }
        }

         adapter = new LatLongHistoryAdapter(getActivity(),R.layout.latlong_historyadapter,latLongDataArrayList);
        listview_locationhistory.setAdapter(adapter);


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.header_listview_loc_history,null);
        listview_locationhistory.addHeaderView(view);


        editsearch = (EditText) v.findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString();
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });



    return v;
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

  private   class  LatLongHistoryAdapter extends ArrayAdapter {


      private List<LatLongData> searchlist = null;
      List<LatLongData> latLongDataArrayList;

      public LatLongHistoryAdapter(Context context, int resource, ArrayList<LatLongData> latLongDatas) {
          super(context, resource, latLongDatas);
          this.searchlist = latLongDatas;
          this.latLongDataArrayList = new ArrayList<>();
          latLongDataArrayList.addAll(searchlist);
      }

      @NonNull
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          if (convertView == null) {

              LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              convertView = layoutInflater.inflate(R.layout.latlong_historyadapter, null, false);

          }

         /* RelativeLayout rela =(RelativeLayout)convertView.findViewById(R.id.relative_lat_long_adapter);
          LinearLayout layout2 = new LinearLayout(getActivity());
          layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
          TextView tv1 = new TextView(getActivity());
          tv1.setGravity(Gravity.CENTER);
          tv1.setText("No Record Found");
          layout2.addView(tv1);
          rela.addView(layout2);
*/
          TextView formno = (TextView) convertView.findViewById(R.id.formno_adapter);
          TextView date = (TextView) convertView.findViewById(R.id.date_adapter);
          TextView lat = (TextView) convertView.findViewById(R.id.lat_adapter);
          TextView longi = (TextView) convertView.findViewById(R.id.longi_adapter);
          ImageView status = (ImageView) convertView.findViewById(R.id.status_adapter);






          LatLongData latLongData = searchlist.get(position);

          List<LatLongData> firstlatlong = db.getFirstLatLong(latLongData.getFormno());

          List<LatLongData> lastlatlong =  db.getLastLatLong(latLongData.getFormno());

         double fdd = distance(Double.parseDouble(firstlatlong.get(position).getLat()),Double.parseDouble(firstlatlong.get(position).getLongi()),Double.parseDouble(lastlatlong.get(position).getLat()),Double.parseDouble(lastlatlong.get(position).getLongi()));

         // Toast.makeText(getActivity(),fdd+"",Toast.LENGTH_LONG).show();


          formno.setText(latLongData.getFormno());
          date.setText(latLongData.getDate());
          String ss = "";
          String s = latLongData.getLat();
          if (s.length()> 4) {

              ss = s.substring(0, 6);
          } else {
              ss = "00.000";
          }

          String sslogi = "";
          String slogi = latLongData.getLongi();
          if (slogi.length()> 4) {
             sslogi = slogi.substring(0, 6);
          } else {
              sslogi = "00.000";
          }


          lat.setText(ss);
          longi.setText(sslogi);

          String getstatus =latLongData.getLatlong_flag() + "";

          if(getstatus.equals("1")){
             // status.setText("Success");
              status.setColorFilter(getResources().getColor(R.color.forestgreen));
          }else {
              //status.setText("Failed");
              status.setColorFilter(getResources().getColor(R.color.red));
          }






          return convertView;
      }

      public void filter(String charText) {
         // charText = charText.toLowerCase(Locale.getDefault());
          searchlist.clear();
          if (charText.length() == 0) {
              searchlist.addAll(latLongDataArrayList);
          }
          else
          {
              for (LatLongData wp : latLongDataArrayList)
              {
                  if (wp.getDate().contains(charText))
                  {

                      searchlist.add(wp);
                  }
              }
          }
          notifyDataSetChanged();
      }


      public String getAddressFromLocation( double latitude,  double longitude) {
        double s=  longitude;
          Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
          String result = null;
          try {
              List<Address> addressList = geocoder.getFromLocation(
                      latitude, longitude, 1);
              if (addressList != null && addressList.size() > 0) {
                  Address address = addressList.get(0);
                  StringBuilder sb = new StringBuilder();
                  for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                      sb.append(address.getAddressLine(i)).append("\n");
                  }
                  sb.append(address.getLocality()).append("\n");
                  sb.append(address.getPostalCode()).append("\n");
                  sb.append(address.getCountryName());
                  result = sb.toString();
              }
          } catch (IOException e) {
             e.printStackTrace();
          }

          return result;
      }

  }












}
