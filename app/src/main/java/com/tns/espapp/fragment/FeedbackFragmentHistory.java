package com.tns.espapp.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.tns.espapp.AppConstraint;
import com.tns.espapp.AttachmentData;
import com.tns.espapp.CaptureData;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.FeedbackRecordData;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragmentHistory extends Fragment  {
    String empid;
    int getID = 0;
    int getID_cap = 0;
    int getFeedbackID = 0;
    private Dialog dialog;
    String selectedValue;
    private DatabaseHandler db;
    private ListView lst_feedback_rec;

    private List<FeedbackRecordData> feedbackRecordDatas = new ArrayList<>();

    public FeedbackFragmentHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feedback_history, container, false);
        db = new DatabaseHandler(getContext());
        SharedPreferences sharedPreferences_setid = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");




/*
ArrayList<String> callLogList = new ArrayList<>();
        callLogList.add("One");
        callLogList.add("two");
        callLogList.add("three");
        callLogList.add("foure");
        callLogList.add("five");
        callLogList.add("six");
        callLogList.add("seven");

        MyRecyclerAdapter  myRecyclerAdapter= new MyRecyclerAdapter(getActivity(), callLogList);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mRecyclerView.setAdapter(myRecyclerAdapter);


        myRecyclerAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             //  Toast.makeText(getActivity(),view.getTag(position)+ "",Toast.LENGTH_LONG).show();
            }
        });

*/

/*
        HashMap<String,CaptureData> map = new LinkedHashMap<>();
        map.put("1",new CaptureData("gdg","1"));
        map.put("2",new CaptureData("22gdg","2"));
        map.put("3",new CaptureData("33gdg","3"));
        map.put("4",new CaptureData("44gdg","4"));
        map.put("5",new CaptureData("55gdg","5"));

        for(Object o : map.keySet()){

            //Toast.makeText(getActivity(),""+ map.get(o),Toast.LENGTH_LONG).show();
        }



        for(Map.Entry<String , CaptureData> s: map.entrySet()){

            CaptureData c =  s.getValue();
            Toast.makeText(getActivity(),""+ s.getKey()+"::"+c.getCaptureImageshow(),Toast.LENGTH_LONG).show();

        }
       Iterator it = map.entrySet().iterator();

        while (it.hasNext()){

            Map.Entry entry = (Map.Entry) it.next();
        }*/



        lst_feedback_rec = (ListView) v.findViewById(R.id.lst_feedback_record);
        feedbackRecordDatas = db.getAllFeedbackRecord();
        // feedbackRecordDatas.add(new FeedbackRecordData("one","two","one","two",0));

        FeedbackRecordAdapter fdAdapter = new FeedbackRecordAdapter(getActivity(), R.layout.feedback_record_history_adapter, feedbackRecordDatas);
        lst_feedback_rec.setAdapter(fdAdapter);
        return v;
    }




    private class FeedbackRecordAdapter extends ArrayAdapter {
        int deepColor = Color.parseColor("#FFFFFF");
        int deepColor2 = Color.parseColor("#DCDCDC");
        //  int deepColor3 = Color.parseColor("#B58EBF");
        private int[] colors = new int[]{deepColor, deepColor2};
        private List<FeedbackRecordData> searchlist = null;
        List<FeedbackRecordData> feedbackRecordDatas_List;



        public FeedbackRecordAdapter(Context context, int resource, List<FeedbackRecordData> feedbackRecordDatas) {
            super(context, resource, feedbackRecordDatas);
            this.searchlist = feedbackRecordDatas;
            this.feedbackRecordDatas_List = new ArrayList<>();
            feedbackRecordDatas_List.addAll(searchlist);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.feedback_record_history_adapter, null, false);
            }
            // List<AttachmentData> attachmentDatas = db.getAllFeedbackAttachment();

            TextView tv_unit = (TextView) convertView.findViewById(R.id.unit_name_adap);
            final TextView tv_ref = (TextView) convertView.findViewById(R.id.ref_feed_rec_adap);
            TextView textView = (TextView) convertView.findViewById(R.id.date_feed_rec_adap);
            ImageView tv_sts = (ImageView) convertView.findViewById(R.id.status_feed_rec_adap);
            TextView btn_attach = (TextView) convertView.findViewById(R.id.btn_uploaded_ada);
            // TextView btn_capture =(TextView) convertView.findViewById(R.id.btn_capture_ada);

            final FeedbackRecordData feedbackRecordData = searchlist.get(position);
            List<AttachmentData> attachmentDatas = db.getAllFeedbackAttachment_BY_Refno(feedbackRecordData.getfEEDBACK_RECORD_REFERENCENO());
            List<CaptureData> captureDatas = db.getAllFeedbackCapture_BY_Refno(feedbackRecordData.getfEEDBACK_RECORD_REFERENCENO());
            int totalsize = attachmentDatas.size() + captureDatas.size();
            tv_unit.setText(feedbackRecordData.getfEEDBACK_RECORD_UNITNAME());
            tv_ref.setText(feedbackRecordData.getfEEDBACK_RECORD_REFERENCENO());
            textView.setText(feedbackRecordData.getfEEDBACK_RECORD_DATE());
            String sts = feedbackRecordData.getfEEDBACK_RECORD_FLAG() + "";
            getFeedbackID = feedbackRecordData.getfEEDBACK_RECORD_INCRIID();


            if (sts.equals("1")) {
                tv_sts.setImageResource(R.drawable.success);
            } else {
                tv_sts.setImageResource(R.drawable.upload);

                tv_sts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<AttachmentData> attachmentDatas1 = db.getAllFeedbackAttachment_BY_Refno(tv_ref.getText().toString());
                        List<CaptureData> captureDatas1 = db.getAllFeedbackCapture_BY_Refno(tv_ref.getText().toString());
                        sendRecordServer(feedbackRecordData, attachmentDatas1, captureDatas1);

                    }
                });


            }

            btn_attach.setText(totalsize + "");

            if (totalsize > 0) {
                btn_attach.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                btn_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<AttachmentData> attachmentDatas1 = db.getAllFeedbackAttachment_BY_Refno(tv_ref.getText().toString());
                        List<CaptureData> captureDatas = db.getAllFeedbackCapture_BY_Refno(tv_ref.getText().toString());

                        showAttachmentPopup(attachmentDatas1, captureDatas);
                    }
                });
            }


            return convertView;
        }


    }


    private void showAttachmentPopup(List<AttachmentData> attachmentDatas, List<CaptureData> captureDatas) {
        dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialoge_show_attachment_popup);

        ListView listView_attach = (ListView) dialog.findViewById(R.id.lst_attachnemtAdapter);
        TextView tv_att = (TextView) dialog.findViewById(R.id.tv_att_TEXT);
        TextView tv_cap = (TextView) dialog.findViewById(R.id.tv_cap_TEXT);

        if (attachmentDatas.size() > 0) {
            tv_att.setVisibility(View.VISIBLE);
        }
        if (captureDatas.size() > 0) {
            tv_cap.setVisibility(View.VISIBLE);
        }


        ListView listView_capture = (ListView) dialog.findViewById(R.id.lst_captureAdapter);
        AttachmentAdapter attachmentAdapter = new AttachmentAdapter(getActivity(), R.layout.attachment_adapter, attachmentDatas);
        CaptureAdapter captureAdapter2 = new CaptureAdapter(getActivity(), R.layout.attachment_adapter, captureDatas);

        listView_attach.setAdapter(attachmentAdapter);
        listView_capture.setAdapter(captureAdapter2);


        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        //  dialog.getWindow().setLayout(width, height / 3);
        // Window window = dialog.getWindow();
        // WindowManager.LayoutParams wlp = window.getAttributes();
        //  window.setGravity(Gravity.CENTER | Gravity.CENTER);
        //  dialog.getWindow().getAttributes().verticalMargin = 0.50F;
        // dialog.getWindow().getAttributes().horizontalMargin = 0.09F;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //  window.setAttributes(wlp);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.show();


    }

    public class AttachmentAdapter extends ArrayAdapter {

        private List<AttachmentData> searchlist = null;

        public AttachmentAdapter(Context context, int resource, List<AttachmentData> attachmentDatas) {
            super(context, resource, attachmentDatas);
            searchlist = attachmentDatas;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.attachment_adapter, null, false);

            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_thumbnail_att);
            TextView tv_text = (TextView) convertView.findViewById(R.id.tv_att_adapter_text);
            ImageView tv_status = (ImageView) convertView.findViewById(R.id.iv_att_adapter_sts);
            final AttachmentData A = searchlist.get(position);

            tv_text.setText(A.getAttachshow());
            //  tv_status.setText(A.getRefNo()+"");

            if ((A.getFlag() + "").equals("1")) {

                tv_status.setImageResource(R.drawable.success);

            } else {
                tv_status.setImageResource(R.drawable.upload);
                tv_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getID = A.getIncriID();
                        selectedValue = "attachment";
                        new Thread() {

                            public void run() {
                                new ftpConnect_Send().uploadFile(new File(A.getAttachsend()));
                            }

                        }.start();

                    }
                });
            }


            Bitmap thumbnail = BitmapFactory.decodeFile(A.getAttachsend());
            imageView.setImageBitmap(thumbnail);


            return convertView;
        }
    }

    public class CaptureAdapter extends ArrayAdapter {

        private List<CaptureData> searchlist = null;

        public CaptureAdapter(Context context, int resource, List<CaptureData> captureDatas) {
            super(context, resource, captureDatas);
            searchlist = captureDatas;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.attachment_adapter, null, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_thumbnail_att);
            TextView tv_text = (TextView) convertView.findViewById(R.id.tv_att_adapter_text);
            ImageView tv_status = (ImageView) convertView.findViewById(R.id.iv_att_adapter_sts);
            final CaptureData A = searchlist.get(position);

            tv_text.setText(A.getCaptureImageshow());


            if ((A.getFlag() + "").equals("1")) {

                tv_status.setImageResource(R.drawable.success);

            } else {
                tv_status.setImageResource(R.drawable.upload);
                tv_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedValue = "capture";
                        getID_cap = A.getIncri_id();
                        new Thread() {

                            public void run() {
                                new ftpConnect_Send().uploadFile(new File(A.getCaptureFilesend()));
                            }

                        }.start();

                    }
                });


            }


            Bitmap thumbnail = BitmapFactory.decodeFile(A.getCaptureFilesend());
            imageView.setImageBitmap(thumbnail);


            return convertView;
        }
    }


    public class ftpConnect_Send {


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

                client.upload(fileName, new MyTransferListener_Send());


            } catch (Exception e) {

                dialog.dismiss();
            /*    List<AttachmentData> datas = db.getAllFeedbackAttachment();
                for(AttachmentData attachmentData1 : datas)
                {
                    db.updateFeedbackAttachment(attachmentData1.getIncriID(), 0);
                    int id = attachmentData1.getFlag();

                    tv.append(id+""+"\n");
                }*/


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


    private class MyTransferListener_Send implements FTPDataTransferListener {

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


            // Yet other length bytes has been transferred since the last time this
            // method was called

            //System.out.println(" transferred ..." + length);
        }

        public void completed() {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (selectedValue.equals("attachment")) {
                        db.updateFeedbackAttachment(getID, 1);
                    }
                    if (selectedValue.equals("capture")) {
                        db.updateFeedbackCapture(getID_cap, 1);
                    }
                    dialog.dismiss();
                    Toast.makeText(getActivity(), " images upload successfully ...", Toast.LENGTH_SHORT).show();
                    Log.v("Capter send  ", "Completed");
                    //  progressDialog.dismiss();
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
                    // progressDialog.dismiss();
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
                    // progressDialog.dismiss();

                }
            });

            // Transfer failed
            System.out.println(" failed ...");
        }

    }


    private void sendRecordServer(FeedbackRecordData feedbackRecordData, List<AttachmentData> attachmentpath, List<CaptureData> capturepatn) {
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


            jsonArrayParameter.put(feedbackRecordData.getfEEDBACK_RECORD_REFERENCENO());
            jsonArrayParameter.put(feedbackRecordData.getfEEDBACK_RECORD_UNITNAME());
            jsonArrayParameter.put(feedbackRecordData.getfEEDBACK_RECORD_BREIF());
            jsonArrayParameter.put(feedbackRecordData.getfEEDBACK_RECORD_DATE());
            jsonArrayParameter.put(empid);
            jsonArrayParameter.put(feedbackRecordData.getfEED_LAT());
            jsonArrayParameter.put(feedbackRecordData.getfEED_LONG());
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

                // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //  getResult_id = jsonObject.getInt("Id");
                        // progressDialog.dismiss();
                        //   db.insert_feedbackREcordData(new FeedbackRecordData(st_Spinner_unit,edt_getreferenceNo.getText().toString(),current_date,edtbrief.getText().toString(),1));
                        db.updateFeedbackRecord(getFeedbackID, 1);
                        getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedbackFragmentHistory()).addToBackStack(null).commit();
                    }


                } catch (JSONException e) {

                    getFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedbackFragmentHistory()).addToBackStack(null).commit();
                    // progressDialog.dismiss();
                    e.printStackTrace();
                }

            /*   int as = attachment_ImageList.size();
               {

                   for(int j = 0; j<as ;j++){
                       progressDialog.show();
                       sendAttachmentServer( attachmentDatas.get(j).getAttachshow());
                      // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag,new FeedBackFragment()).commit();

                   }
                   progressDialog.dismiss();
               }
*//*
               for (CaptureData captueImage: captureDatas){

                   sendCaptureServer(captueImage.getCaptureImageshow());
               }*/


            }
        });


    }


}
