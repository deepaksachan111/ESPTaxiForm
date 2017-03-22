package com.tns.espapp.fragment;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.AttachmentData;
import com.tns.espapp.CaptureData;
import com.tns.espapp.DrawBitmapAll;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.ListviewHelper;
import com.tns.espapp.NetworkConnectionchecker;
import com.tns.espapp.R;
import com.tns.espapp.UnitModel;
import com.tns.espapp.activity.RealPathUtil;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.FeedbackRecordData;
import com.tns.espapp.database.TaxiFormData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


import static android.app.Activity.RESULT_OK;
import static com.tns.espapp.AppConstraint.FTP_PASS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ProgressDialog progressDoalog_att;
    private ProgressDialog progressDoalog_cap;

    private DatabaseHandler db;

    private static int incri = 1;
    private String empid;
    private String current_date;
    private Spinner spinner_unit;
    private String st_Spinner_unit;
    private String getStr_Refno;
    private String getStr_Brief;

    private ImageView iv_addAttachment, iv_capture_Image;
    private LinearLayout linearLayout_add_attachment, linearLayout_add_captureImage;
    private ListView lst_attachment;
    private ListView lst_captureImages;
    int count_addAttachment = 0;
    int count_capture_Image = 0;

    private int PICK_ATTACHMENT_REQUEST = 1;
    private int ADD_CAPTURE_IMAGE = 2;

    private EditText edt_getdate, edt_getreferenceNo, edtbrief;

    private String stgetImage;
    private AttachmentAdapter adapter_attachment;
    private CaptureImageAdapter captureImageAdapter;


    private ArrayList<String> attachment_ImageList = new ArrayList();
    private ArrayList<String> capture_ImageList = new ArrayList();


    ArrayList<String> unit_priflist = new ArrayList<>();
    private Button btn_submit;
    TextView tv;
    private Calendar cal;
    private String realPath;
    private String formated_Date;

    private ArrayList<CaptureData> captureDatas = new ArrayList<>();
    private ArrayList<AttachmentData> attachmentDatas = new ArrayList<>();


    private String lats;
    private String longi;
    private int getResult_id;

    private Geocoder geocoder;

    LocationListener locationListener;
    LocationManager locationManager;

    ArrayList<UnitModel[]> unitList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    ProgressDialog progressBar;
    private int att_length = 0;
    private int progressBarStatus_att = 0;
    private int progressBarStatus_cap = 0;
    private Handler progressBarHandler = new Handler();

    private long fileSize = 0;

    private static int feebback_ID = 0;
    private static int attachmentData1_ID = 0;
    private static int capture1_ID = 0;
    List<FeedbackRecordData> getfeedbackRecord;

    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        setViewByIDS(view);
        getLocation();
        getUnitSpinnerData();
       // getLoctionName();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading data please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        SharedPreferences sharedPreferences_setid = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");
        db = new DatabaseHandler(getActivity());

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        Calendar cal = Calendar.getInstance();
        //System.out.println(dateFormat.format(cal.getTime()));
        current_date = dateFormat.format(cal.getTime());
        edt_getdate.setText(current_date);

        Log.v("getDate", current_date);


        formated_Date = new String(current_date);
        formated_Date = formated_Date.replaceAll("-", "");
        tv = (TextView) view.findViewById(R.id.tv_setresult);


        getfeedbackRecord = db.getAllFeedbackRecord();
        int size = getfeedbackRecord.size();
        List<AttachmentData> attachmentDataslist = db.getAllFeedbackAttachment();
        List<CaptureData> captureDatas = db.getAllFeedbackCaputre();
        if (size > 0) {
            for (FeedbackRecordData aa : getfeedbackRecord) {
                feebback_ID = aa.getfEEDBACK_RECORD_INCRIID();
                //  Toast.makeText(getActivity(),""+aaa,Toast.LENGTH_LONG).show();
            }
        }

        if (attachmentDataslist.size() > 0) {
            for (AttachmentData aa : attachmentDataslist) {
                attachmentData1_ID = aa.getIncriID();

                tv.append(aa.getAttachshow() + "\n");

                //  Toast.makeText(getActivity(),""+aaa,Toast.LENGTH_LONG).show();
            }
        }
        if (captureDatas.size() > 0) {
            for (CaptureData aa : captureDatas) {
                capture1_ID = aa.getIncri_id();
                // Toast.makeText(getActivity(),""+aaa,Toast.LENGTH_LONG).show();
            }
        }


        return view;
    }


    private void setViewByIDS(View view) {
        edt_getdate = (EditText) view.findViewById(R.id.edt_date_feed);
        edt_getreferenceNo = (EditText) view.findViewById(R.id.edt_refenceno_feed);
        edtbrief = (EditText) view.findViewById(R.id.edt_brief_feed);
        btn_submit = (Button) view.findViewById(R.id.btn_submit_feed);


        linearLayout_add_attachment = (LinearLayout) view.findViewById(R.id.linearLayout2);
        // linearLayout_add_captureImage= (LinearLayout)view.findViewById(R.id.linear_add_capture);
        lst_attachment = (ListView) view.findViewById(R.id.listview_add_attachment);
        lst_captureImages = (ListView) view.findViewById(R.id.listview_add_capture);

        spinner_unit = (Spinner) view.findViewById(R.id.spi_unit_feed);


        iv_addAttachment = (ImageView) view.findViewById(R.id.iv_add_attachment);
        iv_capture_Image = (ImageView) view.findViewById(R.id.iv_capture_image);

        // linearLayout_add_attachment = (LinearLayout) view.findViewById(R.id.linear_add_attachment) ;


     /*   unit_priflist.add("BU");
        unit_priflist.add("SU");*/


        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, unit_priflist);
        spinner_unit.setAdapter(arrayAdapter);
        spinner_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_Spinner_unit = spinner_unit.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iv_addAttachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

              /* ViewGroup parentView = (ViewGroup) edt_getreferenceNo.getParent();
                parentView.removeView(edt_getreferenceNo);*/

                if (count_addAttachment < 5) {
                    synchronized (this) {
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        //  intent.setType("file/*");
                        // intent.setType("application/msword,application/pdf");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_ATTACHMENT_REQUEST);
                        count_addAttachment++;
                    }

                } else {

                    Toast.makeText(getActivity(), "Maximum 5 Attachment at a time", Toast.LENGTH_LONG).show();

                }
             /*   ViewGroup parentView = (ViewGroup) v.getParent();
                parentView.removeView(v);*/

                // String arr[] = new String[]{"one","two","three","four","five"};
                // linearLayout_add_attachment.addView(createNewTextView("dggg"));

            }
        });

        iv_capture_Image.setOnClickListener(addCaptureImage);
        btn_submit.setOnClickListener(submitData);


    }


    View.OnClickListener addCaptureImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (count_capture_Image < 5) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ADD_CAPTURE_IMAGE);
                count_capture_Image++;

            } else {
                Toast.makeText(getActivity(), "Maximum 5 Attachment at a time", Toast.LENGTH_LONG).show();

            }


        }
    };

    View.OnClickListener submitData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            NetworkConnectionchecker networkConnectionchecker = new NetworkConnectionchecker(getActivity());
            boolean checkNetwork = networkConnectionchecker.isConnectingToInternet();
            if (!checkNetwork) {

                Toast.makeText(getActivity(), "internet is not active", Toast.LENGTH_LONG).show();
                return;
            }


            if (TextUtils.isEmpty(edt_getreferenceNo.getText().toString())) {
                edt_getreferenceNo.setError("Enter ReferenceNo");
                edt_getreferenceNo.requestFocus();
                getStr_Refno = "";

                return;
            } else {
                getStr_Refno = edt_getreferenceNo.getText().toString();
            }


            for (FeedbackRecordData fd : getfeedbackRecord) {

                if (fd.getfEEDBACK_RECORD_DATE().equals(current_date) && fd.getfEEDBACK_RECORD_REFERENCENO().equals(getStr_Refno)) {
                    edt_getreferenceNo.setError("ReferenceNo Allready Exist ");
                    edt_getreferenceNo.requestFocus();
                    return;
                }

            }


            if (TextUtils.isEmpty(edtbrief.getText().toString())) {
                edtbrief.setError("Please Enter Add Brief");
                edtbrief.requestFocus();
                return;
            } else {

                getStr_Brief = edtbrief.getText().toString();

                new Thread(new Runnable() {

                    public void run() {
                        Looper.prepare();

                        final int a = attachmentDatas.size();
                        if (a > 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressDoalog_att = new ProgressDialog(getActivity());
                                    progressDoalog_att.setMax(a);
                                    progressDoalog_att.setMessage("UploadAttachment....");
                                    progressDoalog_att.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDoalog_att.setCancelable(false);
                                    progressDoalog_att.show();

                                }
                            });

                            for (int j = 0; j < a; j++) {

                                progressBarStatus_att++;
                                db.insertfeedbackAttachment(new AttachmentData(getStr_Refno, attachmentDatas.get(j).getAttachshow(), attachmentDatas.get(j).getAttachsend(), 0));
                                attachmentData1_ID = attachmentData1_ID + 1;
                                ftpConnect_Attachment ftpConnect_attachment = new ftpConnect_Attachment();
                                ftpConnect_attachment.uploadFile(new File(attachmentDatas.get(j).getAttachsend()));
                            }

                        }
                    }

                }).start();


                new Thread(new Runnable() {

                    public void run() {
                        Looper.prepare();
                        final int b = captureDatas.size();


                        if (b > 0) {


                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    progressDoalog_cap = new ProgressDialog(getActivity());
                                    progressDoalog_cap.setMax(b);
                                    progressDoalog_cap.setMessage("Upload Capture Image....");
                                    progressDoalog_cap.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDialog.setCancelable(false);
                                    progressDoalog_cap.show();
                                }

                            });
                            for (int j = 0; j < b; j++) {

                                progressBarStatus_cap++;

                                db.insertfeedbackCapture(new CaptureData(getStr_Refno, captureDatas.get(j).getCaptureImageshow(), captureDatas.get(j).getCaptureFilesend(), 0));
                                capture1_ID = capture1_ID + 1;
                                ftpConnect_Capture ftpConnect = new ftpConnect_Capture();
                                ftpConnect.uploadFile(new File(captureDatas.get(j).getCaptureFilesend()));
                            }

                        }

                        Looper.loop();

                    }


                }).start();
                progressDialog.show();
                sendRecordServer(attachmentDatas, captureDatas);

                db.insert_feedbackREcordData(new FeedbackRecordData(st_Spinner_unit, getStr_Refno, current_date, getStr_Brief, 0, lats, longi));
                feebback_ID = feebback_ID + 1;
                btn_submit.setEnabled(false);
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_ATTACHMENT_REQUEST) {

                Uri uri = data.getData();


                // File filepath = new File(uri.toString());
                // String absolutePath = filepath.getAbsolutePath();

                // File file = new File(absolutePath);
                realPath = RealPathUtil.getPath_File_Attacah(getActivity(), uri);

                // realPath= RealPathUtil.getPathAttachFile(getActivity(),uri);

             /*   // SDK < API11
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());
                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), data.getData());
                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());*/

                File file = new File(realPath);

                stgetImage = file.getName();
                attachment_ImageList.add(stgetImage);
                attachmentDatas.add(new AttachmentData(stgetImage, realPath));

                adapter_attachment = new AttachmentAdapter(getActivity(), R.layout.add_attachment_feedbackfrag_adapter, attachment_ImageList);
                lst_attachment.setAdapter(adapter_attachment);
                ListviewHelper.getListViewSize(lst_attachment);
                if (stgetImage != null) {
                    //  linearLayout_add_attachment.addView(tableLayout(stgetImage), linearLayout_add_attachment.getChildCount());
                }

          /*      try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    String endkmImageEncodeString = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

                    // Log.d(TAG, String.valueOf(bitmap));

                    // ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    // imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            if (requestCode == ADD_CAPTURE_IMAGE) {
                String capturepath = "";

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");
                String formattedTime = df.format(System.currentTimeMillis());
                String v = edt_getreferenceNo.getText().toString();
                if (v.equals("")) {
                    Toast.makeText(getActivity(), "Enter ReferenceNo", Toast.LENGTH_LONG).show();
                    return;
                }

                for (FeedbackRecordData fd : getfeedbackRecord) {

                    if (fd.getfEEDBACK_RECORD_DATE().equals(current_date) && fd.getfEEDBACK_RECORD_REFERENCENO().equals(v)) {
                        edt_getreferenceNo.setError("ReferenceNo Allready Exist ");
                        edt_getreferenceNo.requestFocus();
                        return;
                    }

                }


                if (lats == null) {
                    Toast.makeText(getActivity(), "please wait gps location not found", Toast.LENGTH_LONG).show();

                } else {
                    Double latitude = Double.parseDouble(lats);
                    Double longitude = Double.parseDouble(longi);

                    String totalString = current_date + "\nLat:" + String.format("%.4f", latitude) + ",Long:" + String.format("%.4f", longitude);

                    Bitmap setTextwithImage = DrawBitmapAll.drawTextToBitmap(getContext(), thumbnail, totalString);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    setTextwithImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    String destinationpath = Environment.getExternalStorageDirectory().toString();
                    File destination = new File(destinationpath + "/ESP/FeedBack/");
                    if (!destination.exists()) {
                        destination.mkdirs();
                    }

                    File file = null;
                    FileOutputStream fo;
                    try {
                        // destination.createNewFile();
                        capturepath = empid + "_" + v + "_" + formated_Date + "_" + formattedTime + "_IMG_" + incri + ".jpg";
                        incri++;

                        file = new File(destination, capturepath);

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


                    String startkmImageEncodeString = encodeToBase64(setTextwithImage, Bitmap.CompressFormat.JPEG, 100);
                    String totalcapturepath = destinationpath + "/ESP/FeedBack/" + capturepath;

                    captureDatas.add(new CaptureData(capturepath, totalcapturepath));
                    capture_ImageList.add(capturepath);
                    captureImageAdapter = new CaptureImageAdapter(getActivity(), R.layout.add_attachment_feedbackfrag_adapter, capture_ImageList);
                    lst_captureImages.setAdapter(captureImageAdapter);
                    ListviewHelper.getListViewSize(lst_captureImages);


                }

            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

   /* public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }*/


    private class AttachmentAdapter extends ArrayAdapter {

        ArrayList<String> arrayList;

        public AttachmentAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
            this.arrayList = list;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // super.getView(position, convertView, parent);
            ViewHolder viewHolder = new ViewHolder();

            String s = arrayList.get(position);
            if (convertView == null) {

                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_attachment_feedbackfrag_adapter, null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_name_add_attachment_adapter);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_delete_add_attachment_adapter);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            viewHolder.textView.setText(s);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count_addAttachment = count_addAttachment - 1;

                    arrayList.remove(position);
                    adapter_attachment.notifyDataSetChanged();
                    ListviewHelper.getListViewSize(lst_attachment);
                    attachmentDatas.remove(position);
                    // attachment_ImageList.remove(position);


                }
            });


            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            ImageView imageView;


        }
    }

    private class CaptureImageAdapter extends ArrayAdapter {

        ArrayList<String> arrayList;

        public CaptureImageAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
            this.arrayList = list;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // super.getView(position, convertView, parent);
            if (convertView == null) {

                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.add_attachment_feedbackfrag_adapter, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.tv_name_add_attachment_adapter);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_delete_add_attachment_adapter);
            textView.setText(arrayList.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count_capture_Image = count_capture_Image - 1;
                    arrayList.remove(position);
                    if (incri > 1) {
                        incri--;
                    }
                    captureImageAdapter.notifyDataSetChanged();
                    ListviewHelper.getListViewSize(lst_captureImages);
                    //capture_ImageList.remove(position);
                    captureDatas.remove(position);

                }
            });


            return convertView;
        }
    }


    public class ftpConnect_Attachment {


        public void uploadFile(File fileName) {

            FTPClient client = new FTPClient();

            try {

                client.connect(AppConstraint.FTP_HOST, 21);
                client.login(AppConstraint.FTP_USER, AppConstraint.FTP_PASS);
                client.setType(FTPClient.TYPE_BINARY);
                // client.changeDirectory("TNSSOFT Folder");

                try {

                    // client.changeDirectory("/upload/"); //I want to upload picture in MyPictures directory/folder. you can use your own.
                } catch (Exception e) {

                    //client.createDirectory("/upload/");
                    // client.changeDirectory("/upload/");
                }


                client.upload(fileName, new MyTransferListener_Attachment());


            } catch (Exception e) {


                e.printStackTrace();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDoalog_att.dismiss();
                            Toast.makeText(getActivity(), "server not connect", Toast.LENGTH_SHORT).show();
                        }
                    });

                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

        }

    }


    private class MyTransferListener_Attachment implements FTPDataTransferListener {


        public void started() {

            // Transfer started
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    //         Toast.makeText(getActivity(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                }
            });

            //System.out.println(" Upload Started ...");
        }

        public void transferred(final int length) {


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //           Toast.makeText(getActivity(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
                }
            });


            // Yet other length bytes has been transferred since the last time this
            // method was called

            //System.out.println(" transferred ..." + length);
        }

        public void completed() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int getid = attachmentData1_ID;
                    db.updateFeedbackAttachment(getid, 1);
                    Toast.makeText(getActivity(), "Attachment upload successfully ...", Toast.LENGTH_SHORT).show();
                    Log.v("Attachment send  ", "Completed");
                    progressDoalog_att.incrementProgressBy(progressBarStatus_att);
                    int k = progressBarStatus_att;
                    int p = progressDoalog_att.getMax();

                    if (k == p) {
                        attachment_ImageList.clear();
                        adapter_attachment.notifyDataSetChanged();
                        progressDoalog_att.dismiss();
                    }


                }
            });

            // Transfer completed


            //System.out.println(" completed ..." );
        }

        public void aborted() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getActivity(), " transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
                }
            });

            // Transfer aborted

            //System.out.println(" aborted ..." );
        }

        public void failed() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), " failed...", Toast.LENGTH_SHORT).show();
                    progressDoalog_att.dismiss();

                }
            });

            // Transfer failed
            System.out.println(" failed ...");
        }

    }


    public class ftpConnect_Capture {


        public void uploadFile(File fileName) {


            FTPClient client = new FTPClient();
            try {

                client.connect(AppConstraint.FTP_HOST, 21);
                client.login(AppConstraint.FTP_USER, AppConstraint.FTP_PASS);
                client.setType(FTPClient.TYPE_BINARY);
                // client.changeDirectory("TNSSOFT Folder");

                try {

                    // client.changeDirectory("/upload/"); //I want to upload picture in MyPictures directory/folder. you can use your own.
                } catch (Exception e) {

                    //client.createDirectory("/upload/");
                    // client.changeDirectory("/upload/");
                }

                client.upload(fileName, new MyTransferListener_Capture());


            } catch (Exception e) {
                progressDoalog_cap.dismiss();

                e.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

        }

    }


    /*******
     * Used to file upload and show progress
     **********/


    private class MyTransferListener_Capture implements FTPDataTransferListener {

        public void started() {

            // Transfer started
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //    Toast.makeText(getActivity(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                }
            });

            //System.out.println(" Upload Started ...");
        }

        public void transferred(final int length) {


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //   Toast.makeText(getActivity(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
                }
            });


        }

        public void completed() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int getid = capture1_ID;
                    db.updateFeedbackCapture(getid, 1);
                    Toast.makeText(getActivity(), " images upload successfully ...", Toast.LENGTH_SHORT).show();
                    Log.v("Capter send  ", "Completed");
                    progressDoalog_cap.incrementProgressBy(progressBarStatus_cap);
                    int k = progressBarStatus_cap;
                    int p = progressDoalog_cap.getMax();

                    if (k == p) {
                        capture_ImageList.clear();
                        captureImageAdapter.notifyDataSetChanged();
                        progressDoalog_cap.dismiss();
                    }
                }
            });

            // Transfer completed


            //System.out.println(" completed ..." );
        }

        public void aborted() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getActivity(), " transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
                    progressDoalog_cap.dismiss();
                }
            });

            // Transfer aborted

            //System.out.println(" aborted ..." );
        }

        public void failed() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), " failed...", Toast.LENGTH_SHORT).show();
                    progressDoalog_cap.dismiss();

                }
            });

            // Transfer failed
            System.out.println(" failed ...");
        }

    }


    private void getLocation() {


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lats = String.valueOf(location.getLatitude());
                longi = String.valueOf(location.getLongitude());

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
        //noinspection Missing Permission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);


    }


    private void getUnitSpinnerData() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SqlQuery", "select bunitname from hr_businessunit");
        jsonObject.addProperty("DatabaseName", "TNS_HR");
        jsonObject.addProperty("ServerName", "bkp-server");
        jsonObject.addProperty("UserId", "sanjay");
        jsonObject.addProperty("Password", "tnssoft");


        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait...");
        pd.show();
        pd.setCancelable(false);
        Ion.with(getActivity())
                .load(AppConstraint.FEEDBACK_UNIT)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(jsonObject)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {

                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        String data = null;
                        if (result != null) {
                            data = result.toString();
                            handleResponse(data);
                        }


                      /*  if(data !=null && !data.equals("")) {

                        }*/
                        else {

                            Toast.makeText(getActivity(), "internet is very slow", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                            //getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag,new FeedBackFragment()).commit();
                        }
                        pd.dismiss();
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleResponse(String results) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();


        UnitModel unimodel[] = gson.fromJson(results, UnitModel[].class);
        List<UnitModel[]> list = new ArrayList<>();
        list.add(unimodel);
        for (UnitModel[] modle : list) {
            for (UnitModel s : modle) {

                unit_priflist.add(s.getBunitname());
                arrayAdapter.notifyDataSetChanged();

                // tv.append(s.getBunitname()+"\n");
            }
        }

       /*  Type type = new TypeToken<List<UnitModel>>(){}.getType();
        List<UnitModel> contactList = gson.fromJson(results, type);

       for (UnitModel contact : contactList){
           // Log.i("Contact Details", contact.id + "-" + contact.name + "-" + contact.email);
            tv.append(contact.getBunitname()+"\n");
            Toast.makeText(getActivity(),contact.getBunitname(),Toast.LENGTH_LONG).show();
        }*/


        //  Toast.makeText(getActivity(), unitList.toString(), Toast.LENGTH_LONG).show();

/*

            try {
                JSONArray jsonArray22 = new JSONArray(results);
                for (int i = 0; i < jsonArray22.length(); i++) {


                    JSONObject jsonObject1 = jsonArray22.getJSONObject(i);
                    String name = jsonObject1.getString("bunitname");

                    unit_priflist.add(name);
                    arrayAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
*/


    }

    private void sendRecordServer(ArrayList<AttachmentData> attachmentpath, ArrayList<CaptureData> capturepatn) {
        String strXmlString1_attachment = null;
        String strXmlString1 = null;
        if (attachmentpath.size() != 0) {
            strXmlString1_attachment = "<AttachData>" + " ";

            for (AttachmentData att : attachmentpath) {
                strXmlString1_attachment = strXmlString1_attachment + "<AttachData_ROW>" + " ";
                strXmlString1_attachment = strXmlString1_attachment + "<AttachUrl>" + att.getAttachshow() + "</AttachUrl>" + " ";
                strXmlString1_attachment = strXmlString1_attachment + "</AttachData_ROW>" + " ";
            }
            strXmlString1_attachment = strXmlString1_attachment + "</AttachData>" + " ";
            // strXmlString1_attachment = strXmlString1_attachment.replace("&", "&amp;");
            //  strXmlString1_attachment = strXmlString1_attachment.replaceAll("\\\\", "");
        }

        if (capturepatn.size() != 0) {

            strXmlString1 = "<ImageData>" + " ";
            for (CaptureData capt : capturepatn) {
                strXmlString1 = strXmlString1 + "<ImageData_ROW>" + " ";
                strXmlString1 = strXmlString1 + "<imgUrl>" + capt.getCaptureImageshow() + "</imgUrl>" + " ";
                strXmlString1 = strXmlString1 + "</ImageData_ROW>" + " ";
            }
            strXmlString1 = strXmlString1 + "</ImageData>" + " ";
            strXmlString1 = strXmlString1.replace("\\", "");

        }

        final JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArrayParameter = new JSONArray();


            jsonArrayParameter.put(getStr_Refno);
            jsonArrayParameter.put(spinner_unit.getSelectedItem().toString());
            jsonArrayParameter.put(getStr_Brief);
            jsonArrayParameter.put(edt_getdate.getText().toString());
            jsonArrayParameter.put(empid);
            jsonArrayParameter.put(lats);
            jsonArrayParameter.put(longi);
            jsonArrayParameter.put(strXmlString1_attachment);
            jsonArrayParameter.put(strXmlString1);


            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_Feedback  ");


            jsonObject.put("ParameterList", jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = AppConstraint.COMMONURL;
                Log.v("JsonObject", jsonObject.toString());
                String s = HTTPPostRequestMethod.postMethodforESP(url, jsonObject);
                getResponceRecordServer(s);

                Log.v("Recored Responce", s);
            }
        });

        thread.start();

        // <AttachData>   strXmlString1 = strXmlString1.replace("\\", "");<AttachData_ROW> <AttachUrl>16865:13Feb17:01:30:11 IMG_1.jpg<\/AttachUrl> <\/AttachData_ROW> <AttachData_ROW> <AttachUrl>16865:13Feb17:00:08:05 IMG_1.jpg<\/AttachUrl> <\/AttachData_ROW> <AttachData_ROW> <AttachUrl>16865:13Feb17:00:00:26 IMG_1.jpg<\/AttachUrl> <\/AttachData_ROW> <\/AttachData>

    }

    private void getResponceRecordServer(String s) {
        final String result = s.toString();

        Log.v("getResRecordServer :", result);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                btn_submit.setEnabled(true);
                // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        getResult_id = jsonObject.getInt("Id");
                        progressDialog.dismiss();
                        //   db.insert_feedbackREcordData(new FeedbackRecordData(st_Spinner_unit,edt_getreferenceNo.getText().toString(),current_date,edtbrief.getText().toString(),1));
                        db.updateFeedbackRecord(feebback_ID, 1);
                        edt_getreferenceNo.getText().clear();
                        edtbrief.getText().clear();


                        //  getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedBackFragment()).addToBackStack(null).commit();
                    }


                } catch (JSONException e) {

                    // getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedBackFragment()).addToBackStack(null).commit();
                    progressDialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }

 /*   private void sendAttachmentServer(ArrayList<AttachmentData> imagepath) {
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArrayParameter = new JSONArray();


          *//*  for(AttachmentData attachmentData : imagepath){
                jsonArrayParameter.put(attachmentData.getAttachshow());
            }
*//*


            jsonArrayParameter.put(getResult_id);
           // jsonArrayParameter.put(strXmlString1);

            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_FeedbackAttachemnt");

            jsonObject.put("ParameterList", jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = AppConstraint.COMMONURL;
                Log.v("JsonObject", jsonObject.toString());
                final String s=  HTTPPostRequestMethod.postMethodforESP(url,jsonObject);
                Log.v("Responce Attachment", s);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String res = s.toString();

                    }
                });

            }
        });

        thread.start();

    }


    private void sendCaptureServer(String imagepath) {

        final JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArrayParameter = new JSONArray();


            jsonArrayParameter.put(getResult_id);
            jsonArrayParameter.put(imagepath);
            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_FeedbackImage");
            jsonObject.put("ParameterList", jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = AppConstraint.COMMONURL;
                Log.v("JsonObject", jsonObject.toString());
                final String s=  HTTPPostRequestMethod.postMethodforESP(url,jsonObject);
                Log.v("Responce Attachment", s);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String res = s.toString();
                    }
                });
            }
        });

        thread.start();

    }
*/
/*

    private class uploadFileTask extends AsyncTask<File, Integer, Void> {

          int total =1;
        static final String FTP_HOST = "192.168.1.6";

        static final String FTP_USER = "tnssoft";
        static final String FTP_PASS = "@soft4321";

        //void FTP_DATA_UPLOAD(String FULL_PATH_TO_LOCAL_FILE)
        private ProgressDialog progressDialog;
        int progressInput = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Sending file please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
          //  progressDialog.setMax((int)uploadFilePath1.length());
           // progressDialog.incrementProgressBy((int)((progressInput*100)/(uploadFilePath1.length())));
            progressDialog.show();
        }

        protected Void doInBackground(File... param ) {

            FTPClient client = new FTPClient();

            try {

                client.connect(FTP_HOST, 21);
                client.login(FTP_USER, FTP_PASS);
                client.setType(FTPClient.TYPE_BINARY);
                // client.changeDirectory("TNSSOFT Folder");
                try {

                    // client.changeDirectory("/upload/"); //I want to upload picture in MyPictures directory/folder. you can use your own.
                } catch (Exception e) {

                    //client.createDirectory("/upload/");
                    // client.changeDirectory("/upload/");
                }


              // client.upload(param[0], new MyTransferListener());




            } catch (Exception e) {
                e.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }



                return null;
            }



        @Override
        protected void onProgressUpdate(Integer... progress)   {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }



        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }
    }
    public void startProgressDialog()
    {

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(getActivity());
        progressBar.setCancelable(true);
        progressBar.setMessage("Uploading File...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        // Get the Drawable custom_progressbar
      ///  Drawable customDrawable= res.getDrawable(R.drawable.custom_progressbar);

        // set the drawable as progress drawavle

       // progressBar.setProgressDrawable(customDrawable);

        progressBar.show();

        //reset progress bar status
        progressBarStatus = 0;

        //reset filesize
        fileSize = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {

                    // process some tasks
                    progressBarStatus = fileDownloadStatus();

                    //  sleep 1 second to show the progress
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                // when, file is downloaded 100%,
                if (progressBarStatus >= 100) {

                    // sleep for  2 seconds, so that you can see the 100% of file download
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // close the progress bar dialog
                    progressBar.dismiss();
                }
            }
        }).start();

    }



    //method returns the % of file downloaded
    public int fileDownloadStatus()
    {

        while (fileSize <= 1000000) {

            fileSize++;

            if (fileSize == 100000) {
                return 10;
            } else if (fileSize == 200000) {
                return 20;
            } else if (fileSize == 300000) {
                return 30;
            }
            else if (fileSize == 400000) {
                return 40;
            } else if (fileSize == 500000) {
                return 50;
            } else if (fileSize == 600000) {
                return 60;
            }
            // write your code here

        }

        return 100;

    }

*/
  private void getLoctionName() {
      List<Address> addresses = null;

             double  latitude  =28.567907;
             double longitude =77.325881;
      try {

          geocoder = new Geocoder(getActivity(), Locale.getDefault());
          addresses = geocoder.getFromLocation(latitude, longitude, 1);

          if(addresses.size() > 0) {

              String cityName = addresses.get(0).getAddressLine(0);
              String stateName = addresses.get(0).getAddressLine(1);
              String countryName = addresses.get(0).getAddressLine(2);

         /*  tv_area.setText(addresses.get(0).getAdminArea());
             tv_locality.setText(stateName);
             tv_address.setText(countryName);*/

              Toast.makeText(getActivity(), cityName + "," + stateName + "," + countryName + "," + addresses.get(0).getAdminArea(), Toast.LENGTH_LONG).show();

          }else {
              Toast.makeText(getActivity(),"Not found gps", Toast.LENGTH_LONG).show();

          }

      } catch (IOException e1) {
          e1.printStackTrace();
      }




  }

}






