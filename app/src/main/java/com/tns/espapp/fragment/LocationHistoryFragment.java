package com.tns.espapp.fragment;



import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.tns.espapp.service.SendLatiLongiServerIntentService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class LocationHistoryFragment extends Fragment  {

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




        //  Toast.makeText(getActivity(),fdd+"",Toast.LENGTH_LONG).show();

      /* List<LatLongData> alllatlong = new ArrayList<>();

        double  first_lat = 0,first_longi = 0,diff_lat = 0,total_lat = 0, diff_longi = 0, total_longi = 0,   alllat =0,
         alllong =0;

        // LatLongData( String formno,String date, String lat, String longi, int latlong_flag);

        alllatlong.add(new LatLongData("5","5","28.629987", "77.376712",5));
        alllatlong.add(new LatLongData("7","7","28.525401","77.374279",1));
        alllatlong.add(new LatLongData("9","9","28.616058","77.373593",9));
        alllatlong.add(new LatLongData("11","11","28.605509","77.372906",10));
        alllatlong.add(new LatLongData("11","11","28.7199782","77.367069",10));
        alllatlong.add(new LatLongData("11","11","28.592849","77.358658",10));
        alllatlong.add(new LatLongData("11","11","28.583956","77.365353",10));
        alllatlong.add(new LatLongData("11","11","28.574911","77.356255",10));


        for( int i=0; i < alllatlong.size()-1;i++)
        {

            if( Double.parseDouble(alllatlong.get(i).getLat()) >=Double.parseDouble(alllatlong.get(i+1).getLat() ))
            {
                diff_lat = Double.parseDouble(alllatlong.get(i).getLat()) - Double.parseDouble(alllatlong.get(i + 1).getLat());
                diff_longi = Double.parseDouble(alllatlong.get(i).getLongi()) - Double.parseDouble(alllatlong.get(i + 1).getLongi());
            }

            if( Double.parseDouble(alllatlong.get(i).getLat()) <=Double.parseDouble(alllatlong.get(i+1).getLat() ))
            {

                diff_lat = Double.parseDouble(alllatlong.get(i+1).getLat()) - Double.parseDouble(alllatlong.get(i).getLat());
                diff_longi = Double.parseDouble(alllatlong.get(i+1).getLongi()) - Double.parseDouble(alllatlong.get(i).getLongi());
            }


            total_lat = total_lat + diff_lat;
            total_longi = total_longi + diff_longi;

            first_lat= Double.parseDouble(alllatlong.get(0).getLat());
            first_longi= Double.parseDouble(alllatlong.get(0).getLongi());

            alllat = first_lat + total_lat;
            alllong = first_longi + total_longi;

           // Toast.makeText(getActivity(),total_lat+"",Toast.LENGTH_LONG).show();
            Log.v("total", total_lat+": "+ total_longi);


        }

         double fdd = distenc2(first_lat, first_longi,alllat , alllong);
*/

        List<LatLongData> latLongDataList = db.getAllLatLongORDerBy();
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

      //  editsearch.setText(distance(28.468262, 77.025322, 28.578103, 77.313731)+"");

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
          TextView tv_distence = (TextView) convertView.findViewById(R.id.totalkm_adapter);


          LatLongData latLongData = searchlist.get(position);
          formno.setText(latLongData.getFormno());
          date.setText(latLongData.getDate());

          tv_distence.setText(String.format("%.2f",Double.parseDouble(latLongData.getTotaldis())));
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




    private void saveTextFile( String s){

        try {
            String     h = DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString();
            // this will create a new name everytime and unique
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }
            File filepath = new File(root, h + ".txt");  // file path to save
            FileWriter writer = new FileWriter(filepath);
            writer.append(s);
            writer.flush();
            writer.close();
            String m = "File generated with name " + h + ".txt";

        } catch (IOException e) {
            e.printStackTrace();

        }
    }











}
