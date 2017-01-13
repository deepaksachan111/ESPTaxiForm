package com.tns.espapp.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.LocataionData;
import com.tns.espapp.R;
import com.tns.espapp.activity.LoginActivity;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.LatLongData;
import com.tns.espapp.database.TaxiFormData;
import com.tns.espapp.service.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiFormFragment extends Fragment implements View.OnClickListener {
    GPSTracker gps;
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
    DatabaseHandler db;
    List<TaxiFormData> data;
    double latitude;
    double longitude;

    String lats;
    String longi;

    private boolean b_insert;
    String getDate;

    String getDate_latlong;

    String form_no;
    String paddedkeyid;
    String formated_Date;

    Intent intent;


    public TaxiFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_taxi_form, container, false);
        findIDS(v);
        if (shouldAskPermissions()) {
            askPermissions();
        }


        getLocation();

        if(GPSTracker.isRunning){
            getActivity().startService(intent);

        }


        // getActivity().startService(new Intent(getActivity(), GPSTracker.class));


        intent = new Intent(getActivity(), GPSTracker.class);
        db = new
                DatabaseHandler(getActivity());
        SharedPreferences sharedPreferences_setid = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");


        //  SharedPreferences.Editor editor = sharedPreferences_setid.edit();

        //  db.addTaxiformData(new TaxiFormData(edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(),edtstartkmtext.getText().toString(),edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(),edt_endkm_Image.getText().toString()));

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        getDate = dateFormat.format(cal.getTime());
        edt_settaxiform_date.setText(getDate);


        // Format the date to Strings

        formated_Date = new String(getDate);
        formated_Date = formated_Date.replaceAll("-", "");

        paddedkeyid = String.format("%3s", keyid).replace(' ', '0');

        form_no = empid + "/" + formated_Date + "/" + paddedkeyid;
        tv_form_no.setText(form_no);
        data = db.getAllTaxiformData();
        int a = data.size();
        if (a > 0) {
            for (TaxiFormData datas : data) {
                flag = datas.getFlag();
                keyid = datas.getId();
                getDate = datas.getSelectdate();
                String formno = datas.getFormno();
                String ptype = datas.getProjecttype();
                String vehi_no = datas.getVechicleno();
                String stkm = datas.getStartkm();
                startkmImageEncodeString = datas.getStartkm_image();
                String endkm = datas.getEndkm();
                endkmImageEncodeString = datas.getEndkmimage();

                formated_Date = new String(getDate);
                formated_Date = formated_Date.replaceAll("-", "");

                tv_form_no.setText(empid + "/" + formated_Date + "/" + paddedkeyid);
                if (flag == 1) {

                    keyid++;

                    paddedkeyid = String.format("%3s", keyid).replace(' ', '0');

                    // int id = Integer.parseInt(paddedkeyid);


                    form_no = empid + "/" + formated_Date + "/" + paddedkeyid;

                    tv_form_no.setText(form_no);
                    edtproject_type.getText().clear();
                    edt_vehicle_no.getText().clear();
                    startkmImageEncodeString = "";
                    edt_startkmImage.getText().clear();
                    edtstartkmtext.getText().clear();
                    edtendkmtext.getText().clear();
                    endkmImageEncodeString = "";
                    edt_endkm_Image.getText().clear();

                } else {


                    formated_Date = new String(getDate);
                    formated_Date.replaceAll("-", "");

                    edt_settaxiform_date.setText(formated_Date);
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


                String totalString = getDate + current_time_str + "\nLat :" + String.format("%.6f", latitude) + ",  Long :" + String.format("%.6f", longitude) + "\nStartKM Image ";


                // Bitmap setTextwithImage =    ProcessingBitmap(thumbnail,totalString);
                Bitmap setTextwithImage = drawTextToBitmap(getContext(), thumbnail, totalString);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                setTextwithImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


                String destinationpath = Environment.getExternalStorageDirectory().toString();
                File destination = new File(destinationpath + "/ESP/");
                if (!destination.exists()) {
                    destination.mkdirs();
                }

                File file = null;
                FileOutputStream fo;
                try {
                    // destination.createNewFile();

                    file = new File(destination, paddedkeyid + "::" + getDate + current_time_str + ".jpg");

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

                startkmImageEncodeString = encodeToBase64(setTextwithImage, Bitmap.CompressFormat.JPEG, 75);
                edt_startkmImage.setText(startkmImageEncodeString);

                db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);

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

                String totalString = getDate + current_time_str + "\nLat:" + String.format("%.6f", latitude) + ",Long:" + String.format("%.6f", longitude) + "\nEndKM Image ";

                //  Bitmap setTextwithImage =    ProcessingBitmap(thumbnail,totalString);
                Bitmap setTextwithImage = drawTextToBitmap(getContext(), thumbnail, totalString);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                setTextwithImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


                String destinationpath = Environment.getExternalStorageDirectory().toString();


                File destination = new File(destinationpath + "/ESP/");
                if (!destination.exists()) {
                    destination.mkdirs();
                }

                FileOutputStream fo;
                try {
                    //  file.createNewFile();

                    File file = new File(destination, paddedkeyid + "::" + getDate + current_time_str + ".jpg");
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

                endkmImageEncodeString = encodeToBase64(setTextwithImage, Bitmap.CompressFormat.JPEG, 75);

                edt_endkm_Image.setText(endkmImageEncodeString);

                db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);

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


            if (TextUtils.isEmpty(edtproject_type.getText().toString())) {
                edtproject_type.setError("Please Enter Projecttype");
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

            } else {
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


    private class getDataAsnycTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Upload Data");
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


            String re = s;
            try {

                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                String id = jsonObject.getString("id");


                if (status.equals("true")) {
                    flag = 1;
                    // db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                   // db.deleteSingleRow_LatLong();
                    Toast.makeText(getActivity(), "All Record saved", Toast.LENGTH_LONG).show();

                    if (!b_insert) {
                        b_insert = true;
                        edtproject_type.getText().clear();
                        edt_vehicle_no.getText().clear();
                        startkmImageEncodeString = "";
                        edtstartkmtext.getText().clear();
                        edtendkmtext.getText().clear();
                        endkmImageEncodeString = "";


                    }
                    b_insert = false;
                    flag = 0;
                    btn_close.setEnabled(true);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();


                } else {
                    btn_close.setEnabled(true);
                    Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                btn_close.setEnabled(true);
                Toast.makeText(getActivity(), "Internet is not working", Toast.LENGTH_LONG).show();
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
                    if (s.length() == 1) {

                        if (flag == 1) {
                            flag = 0;
                        }


                        db.addTaxiformData(new TaxiFormData(edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag));

                        //getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GPSTracker.BROADCAST_ACTION));
        /*  FragmentTransaction ft = getFragmentManager().beginTransaction();
                  ft.detach(TaxiFormFragment.this).attach(TaxiFormFragment.this).commit();*/


                    } else {


                        db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag);
                    }
                }

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


                    db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString, flag);

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
                    db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), startkmImageEncodeString, edtendkmtext.getText().toString(), endkmImageEncodeString.toString(), flag);
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

                    db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                }
            }


        });

    }


   /* private Bitmap ProcessingBitmap(Bitmap bm1, String captionString){
        //Bitmap bm1 = null;
        Bitmap newBitmap = null;
        Bitmap.Config config = bm1.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(bm1, 0, 0, null);



        if(captionString != null){

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.BLUE);
            paintText.setTextSize(10);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(10f, 10f, 10f, Color.WHITE);

            Rect rectText = new Rect();
            paintText.getTextBounds(captionString, 0, captionString.length(), rectText);


         *//*   String [] lines=captionString.split("\n");

            float y = rectText.height();

            for (int i = 0; i < lines.length; i++)
            {
                paintText.getTextBounds(lines[i], 0, lines[i].length(), rectText);

                newCanvas.drawText(lines[i], 0, y, paintText);

               // y += rectText.height() * 1.2;  // why 1.2?
            }*//*

           newCanvas.drawText(captionString, 0, rectText.height(), paintText);
          //  Toast.makeText(getActivity(), "L : " + captionString, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "caption empty!",Toast.LENGTH_LONG).show();
        }

        return newBitmap;
    }*/


    public Bitmap drawTextToBitmap(Context gContext,
                                   Bitmap bitmap,
                                   String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (3 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();

        int noOfLines = 0;
        for (String line : gText.split("\n")) {
            noOfLines++;
        }

        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 5;
        int y = (bitmap.getHeight() - bounds.height() * noOfLines);

        Paint mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.transparentBlack));
        int left = 0;
        int top = (bitmap.getHeight() - bounds.height() * (noOfLines + 1));
        int right = bitmap.getWidth();
        int bottom = bitmap.getHeight();
        canvas.drawRect(left, top, right, bottom, mPaint);

        for (String line : gText.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

        return bitmap;
    }


    private void getLocation() {

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                lats = String.valueOf(location.getLatitude());
                longi =String.valueOf(location.getLongitude());
            }
            // gps = new GPSTracker();


            // check if GPS enabled
            //  if(gps.canGetLocation()){
            if (statusOfGPS) {

                if(location == null){
                   Toast.makeText(getActivity(), "GPS not working Location not found" + longitude, Toast.LENGTH_LONG).show();
                }else {

                    lats = String.valueOf(location.getLatitude());
                    longi = String.valueOf(location.getLongitude());

                }

                // \n is for new line
                //   Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                showSettingsAlert();
            }
        }




    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());


        alertDialog.setTitle("GPS Not Enabled");

        alertDialog.setMessage("Do you wants to turn On GPS");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    @Subscribe
    public void getEvent(LocataionData s){

      /*  Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //List<Address> addresses =geocoder.getFromLocation(latitude, longitude, 1);

        try {
            List<Address> addresses = geocoder.getFromLocation(28.5798873, 77.3127933, 1);
            String address = addresses.get(0).getSubLocality();
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
           // txt_paddress.setText(address);
           // txt_city.setText(cityName);
           // txt_state.setText(stateName);

            Toast.makeText(getApplicationContext(),address+","+cityName+stateName,Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //  LocationAddress locationAddress = new LocationAddress();


        // locationAddress.getAddressFromLocation(28.5798873, 77.3127933, getApplicationContext(), new GeocoderHandler());








     /*   if(a>0){
           // db.updateLatLong(String.valueOf(keyid),s.getLatitute(),s.getLongitute(),flag);

        }else{
            db.addTaxiformLatLong(new LatLongData(String.valueOf(keyid),s.getLatitute(),s.getLongitute(),flag));
        }
*/


     //   Toast.makeText(getActivity(),s.getLatitute()+","+s.getLongitute(),Toast.LENGTH_LONG).show();


    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            Toast.makeText(getActivity(), locationAddress, Toast.LENGTH_LONG).show();

            //tvAddress.setText(locationAddress);
        }
    }





  /*  public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }*/



    @Override
    public void onPause() {
        super.onPause();
      getActivity().unregisterReceiver(broadcastReceiver);
      //GPSTracker.BUS.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
      //  GPSTracker.BUS.register(this);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GPSTracker.BROADCAST_ACTION));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);

    }


   private List<String> getlatlist(){
        List<String> list = new ArrayList<>();
       list.add("28.5797316666667");
       list.add("28.5832216666667");
       list.add("28.446415");
       list.add("28.440425");
       list.add("28.482555");
       list.add("28.482375");
       list.add("28.6386916666667");
       list.add("28.7651116666667");
       list.add("28.7651083333333");
       list.add("28.5718083333333");
       list.add("28.57181");
       list.add("28.5718116666667");
       list.add("28.5718133333333");
       list.add("28.57181");
       list.add("28.5718066666667");
       list.add("28.5718133333333");
       list.add("28.571815");

       return list;

    }

    private List<String> getlongilist(){
        List<String> list = new ArrayList<>();
        list.add("77.314955");
        list.add("77.3060416666667");
        list.add("77.5976583333333");
        list.add("77.6421766666667");
        list.add("77.4576783333333");
        list.add("77.4576783333333");
        list.add("77.5002916666667");
        list.add("77.50029");
        list.add("77.3339983333333");
        list.add("77.3339916666667");
        list.add("77.333985");
        list.add("77.3339816666667");
        list.add("77.33398");
        list.add("77.33398");
        list.add("77.3339816666667");
        list.add("77.3339816666667");


        return list;

    }




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // mContact = (Contact)getIntent().getExtras().getSerializable(EXTRA_CONTACT);

            LocataionData locataionData = (LocataionData) intent.getExtras().getSerializable("EXTRA");

            lats = String.valueOf(locataionData.getLatitute());
            longi = String.valueOf(locataionData.getLongitute());
            boolean status = locataionData.isStatus();

            if(status){

                latlongtableflag =1;
                db.addTaxiformLatLong(new LatLongData(form_no,getDate,lats,longi,latlongtableflag));

                new  getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);
             /*   List<LatLongData> latLongDataList = db.getAllLatLong();
                int a =latLongDataList.size();
                if(a > 0){
                    for(LatLongData latLongData : latLongDataList){

                        int flags = latLongData.getLatlong_flag();
                        if(flags == 0){
                            latlongtableflag =1;
                            db.addTaxiformLatLong(new LatLongData(form_no,getDate,lats,longi,flag));
                            new  getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);
                        }
                    }
                    }*/

            }else {
                latlongtableflag =0;
                db.addTaxiformLatLong(new LatLongData(form_no,getDate,lats,longi,latlongtableflag));
            }



           // new  getDataTrackTaxiAsnycTask().execute(AppConstraint.TAXITRACKROOT);




          /*  List<LatLongData> latLongDataList = db.getAllLatLong();
            int a =latLongDataList.size();
            if(a > 0){
                for(LatLongData latLongData : latLongDataList){

                    String latiii = latLongData.getLat();
                }
            }*/


          //  Toast.makeText(getActivity(), lats+","+longi, Toast.LENGTH_LONG).show();

        }
    };






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

    private JSONObject JsonParameterTaxiTrack() {



        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");


        try {

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");

            Date dtt = df.parse(getDate);
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



    }
