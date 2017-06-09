package com.tns.espapp.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.DrawBitmapAll;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.TaxiFormData;
import com.tns.espapp.service.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiFormFragment extends Fragment implements View.OnClickListener,
        com.google.android.gms.location.LocationListener,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText edt_settaxiform_date, edt_startkmImage, edt_endkm_Image, edtstartkmtext, edtendkmtext, edtproject_type, edt_vehicle_no;
    private int flag = 0;
    private int latlongtableflag;
    private Button btn_close;
    int REQUEST_CAMERA = 0;
    private static final int SELECT_PICTURE = 1;
    private TextView tv_form_no;

    private String startkmImageEncodeString = "", endkmImageEncodeString = "";
    String empid;


    int keyid = 1;

    int incri_id =0 ;
    DatabaseHandler db;
    List<TaxiFormData> data;
    double latitude;
    double longitude;


    boolean checkNetwork = false;
    private boolean b_insert;


    String current_date;

    private int gpstimesec;
    private boolean getGPSAllowed;

    String form_no;
    String paddedkeyid;
    String formated_Date;

    Intent intent;

    private String lats;
    private String longi;
    LocationListener locationListener;
    LocationManager locationManager;
    boolean statusOfGPS;


    private  SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final long INTERVAL = 1000 * 5 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 2 * 1; // 1 minute
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
   //ImageView iv_status;

    public TaxiFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* try {

                getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GPSTracker.BROADCAST_ACTION));


        }catch (Exception e){

            e.printStackTrace();
        }*/

    }




    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_taxi_form, container, false);

        Toolbar toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
        // iv_status =(ImageView) toolbar. findViewById(R.id.status_taxiform);

        findIDS(v);
     /*   if (shouldAskPermissions()) {
            askPermissions();
        }*/

     // boolean b = GPSTracker.isRunning;
        sharedPreferences     = getActivity().getSharedPreferences("SERVICE", Context.MODE_PRIVATE);
        intent = new Intent(getActivity(), GPSTracker.class);
        /*if(!b){
            getActivity().startService(intent);

        }*/
        getLocation();

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        db = new DatabaseHandler(getActivity());


        SharedPreferences sharedPreferences_setid = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");
        getGPSAllowed = sharedPreferences_setid.getBoolean("gpsallowed",false);
        gpstimesec = sharedPreferences_setid.getInt("gpstimesec",0);


        //  SharedPreferences.Editor editor = sharedPreferences_setid.edit();

        //  db.addTaxiformData(new TaxiFormData(edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(),edtstartkmtext.getText().toString(),edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(),edt_endkm_Image.getText().toString()));

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        current_date = dateFormat.format(cal.getTime());
        edt_settaxiform_date.setText(current_date);


        // Format the date to Strings

        formated_Date = new String(current_date);
        formated_Date = formated_Date.replaceAll("-", "");

        paddedkeyid = String.format("%3s", keyid).replace(' ', '0');

        form_no = empid + "/" + formated_Date + "/" + paddedkeyid;
        tv_form_no.setText(form_no);
        data = db.getAllTaxiformData();
        int a = data.size();


        if(a >200){
            db.deleteSomeRow_Taxiform();
        }



        if (a > 0) {
            for (TaxiFormData datas : data) {
                flag = datas.getFlag();
                incri_id = datas.getId();
                keyid = datas.getKeyid();
                String getDate2 = datas.getSelectdate();
                form_no = datas.getFormno();
                String ptype = datas.getProjecttype();
                String vehi_no = datas.getVechicleno();
                String stkm = datas.getStartkm();
                startkmImageEncodeString = datas.getStartkm_image();
                String endkm = datas.getEndkm();
                endkmImageEncodeString = datas.getEndkmimage();


                if (flag == 1 || flag == 2) {

                    if(!current_date.equals(getDate2)){
                     keyid=1;
                    }
                    else{
                         keyid++;
                    }


                    paddedkeyid = String.format("%3s", keyid).replace(' ', '0');
                    // int id = Integer.parseInt(paddedkeyid);

                    formated_Date = new String(current_date);
                    formated_Date = formated_Date.replaceAll("-", "");
                    form_no = empid + "/" + formated_Date + "/" + paddedkeyid;

                    tv_form_no.setText(form_no);
                    edt_settaxiform_date.setText(current_date);
                    edtproject_type.getText().clear();
                    edt_vehicle_no.getText().clear();
                    startkmImageEncodeString = "";
                    edt_startkmImage.getText().clear();
                    edtstartkmtext.getText().clear();
                    edtendkmtext.getText().clear();

                    endkmImageEncodeString = "";
                    edt_endkm_Image.getText().clear();

                } else {

                 /*   formated_Date = new String(getDate2);
                    formated_Date = formated_Date.replaceAll("-", "");
                    paddedkeyid = String.format("%3s", keyid).replace(' ', '0');

                    form_no =empid + "/" + formated_Date + "/" + paddedkeyid;*/

                    current_date = getDate2;
                    tv_form_no.setText(form_no);
                    edt_settaxiform_date.setText(getDate2);
                    edtproject_type.setText(ptype);
                    edt_vehicle_no.setText(vehi_no);
                    edtstartkmtext.setText(stkm);
                    edtendkmtext.setText(endkm);
                    edt_startkmImage.setText(startkmImageEncodeString);
                    edt_endkm_Image.setText(endkmImageEncodeString);

                }


                // getsearchList.add(new AddBusData(busno,source, desitnatin,arrival,depature,fare));
            }
        } else {

            //   Toast.makeText(getActivity(),"No Record Found",Toast.LENGTH_LONG).show();
        }


        //  String yy = "http://tnssofts.com/espservice/TnsEspService.svc/ExecuteJson";
        //new getDataAsnycTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,yy);


        allEdittexform();


 /*  List<LatLongData> latLongDataList = db.getAllLatLong();
            int aaa =latLongDataList.size();
            if(aaa > 0){
                for(LatLongData latLongData : latLongDataList){

                    String latiii = latLongData.getLat();
                    String longi = latLongData.getLongi();
                }
            }
*/


        return v;
    }

    private void findIDS(View v) {

        tv_form_no = (TextView) v.findViewById(R.id.tv_form_no);
        edt_settaxiform_date = (EditText) v.findViewById(R.id.edt_settaxiform_date);
        edt_startkmImage = (EditText) v.findViewById(R.id.edt_startkm_image);
        edt_endkm_Image = (EditText) v.findViewById(R.id.edt_endkm_image);
        edtstartkmtext = (EditText) v.findViewById(R.id.edt_startkm_text);
        edtendkmtext = (EditText) v.findViewById(R.id.edt_endkmtext);
        edtproject_type = (EditText) v.findViewById(R.id.edt_project_type);
        edt_vehicle_no = (EditText) v.findViewById(R.id.edt_vehicle_no);
        btn_close = (Button) v.findViewById(R.id.btn_close_taxiform);

        edt_startkmImage.setOnClickListener(this);
        edt_endkm_Image.setOnClickListener(this);
        edtstartkmtext.setOnClickListener(this);
        edtendkmtext.setOnClickListener(this);
        edtproject_type.setOnClickListener(this);
        edt_vehicle_no.setOnClickListener(this);
        btn_close.setOnClickListener(this);


    }

    private void selectImage(String Value) {


        if (Value.equals("start")) {
      /*      final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }

                    else if (items[item].equals("Choose from Library")) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image*//*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_PICTURE);
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();*/

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);


        }
        if (Value.equals("end")) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 2);

        /*    final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 2);
                    } else if (items[item].equals("Choose from Library")) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image*//*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                3);
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();*/
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
          /*  if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
               String uri = selectedImageUri.toString();
             File file =   new File(selectedImageUri.getPath());

               // Bitmap bitmap= BitmapFactory.decodeFile(uri);

                try {
                  Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // setImg.setImageBitmap(bitmap);
              //  setImg.setImageURI(selectedImageUri);
                edt_startkmImage.setText(file.toString());
               // encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,50);


            } else if */

            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data, "start");



         /*   if (requestCode == 3) {
                Uri selectedImageUri = data.getData();
                String uri = selectedImageUri.toString();
                File file =   new File(selectedImageUri.getPath());

                // Bitmap bitmap= BitmapFactory.decodeFile(uri);

                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // setImg.setImageBitmap(bitmap);
                //  setImg.setImageURI(selectedImageUri);
                edt_endkm_Image.setText(file.toString());
                // encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,50);


            } else*/
            if (requestCode == 2)
                onCaptureImageResult(data, "end");


        }
    }


    private void onCaptureImageResult(Intent data, String name) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        if (name.equals("start")) {

            SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
            String current_time_str = time_formatter.format(System.currentTimeMillis());

            if (lats == null) {
                Toast.makeText(getActivity(), "please wait gps location not found", Toast.LENGTH_LONG).show();

            } else {
                latitude = Double.parseDouble(lats);
                longitude = Double.parseDouble(longi);


                String totalString = current_date + current_time_str + "\nLat :" + String.format("%.4f", latitude) + ",  Long :" + String.format("%.4f", longitude) + "\nStartKM Image ";


                // Bitmap setTextwithImage =    ProcessingBitmap(thumbnail,totalString);
                Bitmap setTextwithImage = DrawBitmapAll.drawTextToBitmap(getContext(), thumbnail, totalString);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                setTextwithImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);


                String destinationpath = Environment.getExternalStorageDirectory().toString();
                File destination = new File(destinationpath + "/ESP/");
                if (!destination.exists()) {
                    destination.mkdirs();
                }

                File file = null;
                FileOutputStream fo;
                try {
                    // destination.createNewFile();

                    file = new File(destination, paddedkeyid + "::" + current_date +"_"+ current_time_str + ".jpg");

                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1));
                String path = (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1));


                // startkmImageEncodeString = encodeToBase64(thumbnail, Bitmap.CompressFormat.JPEG, 50);

                startkmImageEncodeString = encodeToBase64(setTextwithImage, Bitmap.CompressFormat.JPEG, 80);
                edt_startkmImage.setText(startkmImageEncodeString);

                db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);

            }
        }
        if (name.equals("end")) {


            // edt_profilephoto.setText("");
            //   edt_endkm_Image.setText(path);

           /* StringBuilder sb = new StringBuilder();
            sb.append("Lat: "+ latitude );
            sb.append(System.getProperty("line.separator")); // Add Explicit line separator each time
            sb.append("\n");
            sb.append("Long :" + longitude);

            String totalString = sb.toString();
*/

            SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
            String current_time_str = time_formatter.format(System.currentTimeMillis());
            if (lats == null) {
                Toast.makeText(getActivity(), "please wait gps location not found", Toast.LENGTH_LONG).show();

            } else {
                latitude = Double.parseDouble(lats);
                longitude = Double.parseDouble(longi);

                String totalString = current_date + current_time_str + "\nLat:" + String.format("%.4f", latitude) + ",Long:" + String.format("%.4f", longitude) + "\nEndKM Image ";

                //  Bitmap setTextwithImage =    ProcessingBitmap(thumbnail,totalString);
                Bitmap setTextwithImage = DrawBitmapAll.drawTextToBitmap(getContext(), thumbnail, totalString);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                setTextwithImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);


                String destinationpath = Environment.getExternalStorageDirectory().toString();


                File destination = new File(destinationpath + "/ESP/");
                if (!destination.exists()) {
                    destination.mkdirs();
                }

                FileOutputStream fo;
                try {
                    //  file.createNewFile();

                    File file = new File(destination, paddedkeyid + "::" + current_date +"_"+ current_time_str + ".jpg");
                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                //  Uri tempUri = getImageUri(getActivity(), thumbnail);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                // File finalFile = new File(getRealPathFromURI(tempUri));

                System.out.println(destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("\\") + 1));
                String path = (destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("\\") + 1));


                // endkmImageEncodeString= encodeToBase64(thumbnail, Bitmap.CompressFormat.JPEG,50);

                endkmImageEncodeString = encodeToBase64(setTextwithImage, Bitmap.CompressFormat.JPEG, 80);

                edt_endkm_Image.setText(endkmImageEncodeString);

                db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);

            }
            // setImg.setImageBitmap(thumbnail);
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    @Override
    public void onClick(View v) {



        if (v == edt_startkmImage) {

            selectImage("start");

        }

        if (v == edt_endkm_Image) {

            selectImage("end");
        }

        if (v == btn_close) {
            int stkm = 0;
            int endkm =0;
            if(!edtstartkmtext.getText().toString().equals("")) {
                 stkm = Integer.parseInt(edtstartkmtext.getText().toString());

            }
            if(!edtendkmtext.getText().toString().equals("")) {
                 endkm = Integer.parseInt(edtendkmtext.getText().toString());

            }
           // int endkm = Integer.parseInt(edtendkmtext.getText().toString());

            if (TextUtils.isEmpty(edtproject_type.getText().toString())) {
                edtproject_type.setError("Please Enter ProjectType");
                edt_vehicle_no.getText().clear();
                edtstartkmtext.getText().clear();
                edtendkmtext.getText().clear();
                edtproject_type.requestFocus();
                return;
            } else if (TextUtils.isEmpty(edt_vehicle_no.getText())) {
                edt_vehicle_no.setError("Please Enter Vehicle No");
                edt_vehicle_no.requestFocus();
                return;

            } else if (TextUtils.isEmpty(edtstartkmtext.getText())) {
                edtstartkmtext.setError("Please Enter Start KM");
                edtstartkmtext.requestFocus();
                return;

           } else if (TextUtils.isEmpty(edt_startkmImage.getText())) {
                // edtstartkmtext.setError("Please upload StartKM Image");
                //edtstartkmtext.requestFocus();
                Toast.makeText(getActivity(), "Capture Image for Start KM", Toast.LENGTH_LONG).show();
                return;

            }  else if (TextUtils.isEmpty(edtendkmtext.getText())) {
                edtendkmtext.setError("Please Enter End KM");
                edtendkmtext.requestFocus();
                return;
            }

                else if (TextUtils.isEmpty(edt_endkm_Image.getText())) {
                // edt_endkm_Image.setError("Please upload End KM Image");
                // edt_endkm_Image.requestFocus();
                Toast.makeText(getActivity(), "Capture Image for End KM", Toast.LENGTH_LONG).show();
                return;

            }

            else if ( stkm > endkm) {
                 edtendkmtext.setError("Endkm not lessthan Startkm");
                 edtendkmtext.requestFocus();
                Toast.makeText(getActivity(), "Endkm not lessthan Startkm", Toast.LENGTH_LONG).show();
                return;

            }

            else {
                btn_close.setEnabled(false);


                String url = AppConstraint.TAXIFORMURL;
                new getDataAsnycTask().execute(url);


            }
        }


    }


    public boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    public void askPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    public JSONObject JSonobjParameter() {
        JSONObject jsonObject = new JSONObject();
        try {

    /*     JSONArray jsonArrayParameter = new JSONArray();
         jsonArrayParameter.put("nov_2016");
         jsonArrayParameter.put("2016");
         jsonArrayParameter.put("11");*/


            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("ftProject", edtproject_type.getText().toString());
            jsonObject.put("fnStartkm", edtstartkmtext.getText().toString());
            jsonObject.put("ftStartkmImgUrl", startkmImageEncodeString);
            jsonObject.put("fnEndkm", edtendkmtext.getText().toString());
            jsonObject.put("ftEndkmImgUrl", endkmImageEncodeString);
            jsonObject.put("Empid", empid);
            jsonObject.put("ftvehicleNo", edt_vehicle_no.getText().toString());
            jsonObject.put("ftTaxiFormNo", form_no);
            jsonObject.put("fdDate", edt_settaxiform_date.getText().toString());


            // jsonObject.put("spName","USP_Get_Attendance");
            //  jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class getDataAsnycTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please Wait...");
            pd.setMessage("Uploaded Records");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String s = HTTPPostRequestMethod.postMethodforESP(params[0], JSonobjParameter());
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            getActivity().stopService(intent);
          //  iv_status.setVisibility(View.GONE);
           // GPSTracker.isRunning= false;


            String re = s;
            try {

                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                String id = jsonObject.getString("id");


                if (status.equals("true")) {
                    flag = 1;
                    // db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    Toast.makeText(getActivity(), "Uploaded Successfully...", Toast.LENGTH_LONG).show();

                    if (!b_insert) {
                        b_insert = true;

                        edtproject_type.getText().clear();
                        edt_vehicle_no.getText().clear();
                        startkmImageEncodeString = "";
                        edtstartkmtext.getText().clear();
                        edt_startkmImage.getText().clear();
                        edt_endkm_Image.getText().clear();
                        edtendkmtext.getText().clear();
                        endkmImageEncodeString = "";


                    }

                    btn_close.setEnabled(true);
                    getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).addToBackStack(null).commit();

                   /* FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();*/


                } else {

                    Toast.makeText(getActivity(), "Internet is not working", Toast.LENGTH_LONG).show();
                    flag = 2;
                    // db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);


                    if (!b_insert) {
                        b_insert = true;

                        edtproject_type.getText().clear();
                        edt_vehicle_no.getText().clear();
                        startkmImageEncodeString = "";
                        edtstartkmtext.getText().clear();
                        edt_startkmImage.getText().clear();
                        edt_endkm_Image.getText().clear();
                        edtendkmtext.getText().clear();
                        endkmImageEncodeString = "";


                    }

                    btn_close.setEnabled(true);
                    getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).addToBackStack(null).commit();

                 /*   FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();*/
                }


            } catch (JSONException e) {

                Toast.makeText(getActivity(), "internet is very slow please try again", Toast.LENGTH_LONG).show();
                flag = 2;
                // db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);

                  boolean b = b_insert;
                if (!b_insert) {
                    b_insert = true;

                    edtproject_type.getText().clear();
                    edt_vehicle_no.getText().clear();
                    startkmImageEncodeString = "";
                    edtstartkmtext.getText().clear();
                    edt_startkmImage.getText().clear();
                    edt_endkm_Image.getText().clear();
                    edtendkmtext.getText().clear();
                    endkmImageEncodeString = "";


                }


                btn_close.setEnabled(true);
                getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).addToBackStack(null).commit();

             /*   FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();
             */
                e.printStackTrace();
            }


        }
    }


    private void allEdittexform() {

       edtproject_type.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!b_insert) {

                    if(s.length()== 0){

                        db.deleteSingleRowTaxiformData(form_no);
                    }

                    if (s.length() == 1) {

                        if (flag == 1 || flag == 2) {
                            flag = 0;
                        }
                         if(!GPSTracker.isRunning){

                           /*  intent.putExtra("formno",form_no);
                             intent.putExtra("getdate", current_date);
                             intent.putExtra("empid",empid);*/
                              if(!getGPSAllowed ) {
                                  editor = sharedPreferences.edit();
                                  editor.putString("formno", form_no);
                                  editor.putString("getdate", current_date);
                                  editor.putString("empid", empid);
                                  editor.commit();

                                  getActivity().startService(intent);

                              }
                         }


                        db.addTaxiformData(new TaxiFormData(keyid,edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag));
                        incri_id=incri_id+1;

           /*  FragmentTransaction ft = getFragmentManager().beginTransaction();
                  ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();*/


                    } else {


                        db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag);
                    }
                }

            }


        });

     /*   edtproject_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (flag == 1 || flag == 2) {
                        flag = 0;
                    }
                    if(!GPSTracker.isRunning){

                        intent.putExtra("formno",form_no);
                        intent.putExtra("getdate", current_date);
                        intent.putExtra("empid",empid);
                        getActivity().startService(intent);
                    }

                    db.addTaxiformData(new TaxiFormData(keyid,edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag));
                    incri_id=incri_id+1;
                }
            }
        });*/
        edt_vehicle_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag);

            }
        });


        edt_vehicle_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!b_insert) {

                    db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag);

                }

            }

        });


        edtstartkmtext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!b_insert) {
                    db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString.toString(), flag);
                }
            }


        });

        edtendkmtext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!b_insert) {

                    db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                }
            }


        });

    }


    private void getLocation() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
             lats  =  String.valueOf(location.getLatitude());
             longi =  String.valueOf(location.getLongitude());

             //   Toast.makeText(getActivity(),"GPS Enabled",300).show();
                // tv.append(s);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };


        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //noinspection Missing Permission

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);



        }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();

            //stopLocationUpdates();


   //  getActivity().unregisterReceiver(broadcastReceiver);
      //GPSTracker.BUS.unregister(this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d("TaxiFormFragment", "Location update stopped .......................");
    }
    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d("TaxiFormFragment", "Location update resumed .....................");
        }

      //  GPSTracker.BUS.register(this);
        //getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GPSTracker.BROADCAST_ACTION));
    }



    @Override
    public void onDestroy(){
        super.onDestroy();



       try {

          // getActivity().unregisterReceiver(broadcastReceiver);

        }catch (Exception e){

            e.printStackTrace();
        }


    }





   private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           // mContact = (Contact)getIntent().getExtras().getSerializable(EXTRA_CONTACT);

           boolean status = intent.getBooleanExtra("EXTRA",false);
          if(status == true) {
            //  iv_status.setVisibility(View.VISIBLE);
             // iv_status.setBackgroundResource(R.drawable.blink_animation);
              // AnimationDrawable frameAnimation = (AnimationDrawable) iv_status.getBackground();

              // Start the animation (looped playback by default).
             // frameAnimation.start();

             // Toast.makeText(getActivity(), "running" + "", Toast.LENGTH_LONG).show();
          }else {
            //  iv_status.setVisibility(View.GONE);
              Toast.makeText(getActivity(), "not running" + "", Toast.LENGTH_LONG).show();
          }
           // new  getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);


       }



   };



/*
    private class getDataTrackTaxiAsnycTask extends AsyncTask<String,Void,String>{

        ProgressDialog pd =  new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  pd.setMessage("Loading");
            pd.setCancelable(true);
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
            pd.dismiss();
            String re  = s;
            try {

                JSONObject jsonObject = new JSONObject(s);
                String status =jsonObject.getString("status");
                String id = jsonObject.getString("ID");


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

            Date dtt = df.parse(current_date);
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


            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_Taxi_Lat_Log");


      *//*      jsonObject.put("ftTaxiFormNo", form_no);
            jsonObject.put("Empid", empid);
            jsonObject.put("ftLat", lats);
            jsonObject.put("ftLog", longi);
            jsonObject.put("fdCreatedDate", current_date);
            jsonObject.put("fbStatus", flag);
            jsonObject.put("fnTaxiFormId", "0");*//*



            // jsonObject.put("spName","USP_Get_Attendance");
             jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }*/

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TaxiFormFrgament", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("TaxiFormFrgament", "Location update started ..............: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        lats  =  String.valueOf(location.getLatitude());
        longi =  String.valueOf(location.getLongitude());



    }





    }
