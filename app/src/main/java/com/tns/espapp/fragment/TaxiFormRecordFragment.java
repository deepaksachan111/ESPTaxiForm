package com.tns.espapp.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.R;
import com.tns.espapp.activity.MapWebViewActivity;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.TaxiFormData;
import com.tns.espapp.service.SendLatiLongiServerIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiFormRecordFragment extends Fragment {


    private DatabaseHandler db;
    private TaxiFormRecordHistoryAdapter adapter;
    private     ArrayList<TaxiFormData> taxiFormDataArrayList ;
    private    ListView listview_taxirecord_history;
    private String empid,getstatus;
    private EditText editsearch;
    private int getkey_id;
    public TaxiFormRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_taxi_form_record, container, false);
        db = new DatabaseHandler(getActivity());
        SharedPreferences sharedPreferences_setid = getActivity().getSharedPreferences("ID", Context.MODE_PRIVATE);
        empid = sharedPreferences_setid.getString("empid", "");
        taxiFormDataArrayList = new ArrayList<>();
        listview_taxirecord_history=(ListView)v.findViewById(R.id.listview_taxiform_history);
        List<TaxiFormData> taxiformrecordDataList = db.getAllTaxiformData();
        int size = taxiformrecordDataList.size();



        if(size >0){
            for(TaxiFormData taxiFormData : taxiformrecordDataList){
                taxiFormDataArrayList.add(taxiFormData);
            }
        }


        adapter = new TaxiFormRecordHistoryAdapter(getActivity(),R.layout.taxiform_record_history_adapter,taxiFormDataArrayList);
        listview_taxirecord_history.setAdapter(adapter);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.header_listview_taxiform_record,null);
        listview_taxirecord_history.addHeaderView(view);

        listview_taxirecord_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // TaxiFormData taxiFormData = (TaxiFormData)adapter.getItem(position);
             /* //  TaxiFormData s = (TaxiFormData) parent.getItemAtPosition(position);
              // String ss=  s.getFormno();
               TaxiFormData taxiFormDatas =  taxiFormDataArrayList.get(position-1);
               String ss22=  taxiFormDatas.getFormno();
               // db.deleteSingleRowTaxiformData(ss22);
                taxiFormDataArrayList.remove(position-1);
                adapter.notifyDataSetChanged();*/


            }
        });



        editsearch = (EditText) v.findViewById(R.id.search_taxirecord);

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

    private   class  TaxiFormRecordHistoryAdapter extends ArrayAdapter {
        TaxiFormData  taxiFormData;
        int deepColor = Color.parseColor("#FFFFFF");
        int deepColor2 = Color.parseColor("#DCDCDC");
      //  int deepColor3 = Color.parseColor("#B58EBF");
        private int[] colors = new int[]{deepColor, deepColor2};
        private List<TaxiFormData> searchlist = null;
        List<TaxiFormData> taxiForm_DataArrayList;

        public TaxiFormRecordHistoryAdapter(Context context, int resource, ArrayList<TaxiFormData> latLongDatas) {
            super(context, resource, latLongDatas);
            this.searchlist = latLongDatas;
            this.taxiForm_DataArrayList = new ArrayList<>();
            taxiForm_DataArrayList.addAll(searchlist);
        }

        private class ViewHolder{

            TextView formno ;
            TextView date ;
            TextView id ;
            TextView projecttype;
            TextView vihecleno;
            ImageView status ;
            TextView startkm ;
            TextView endkm;


        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            taxiFormData = searchlist.get(position);
            ViewHolder viewHolder = new ViewHolder();

            if (convertView == null) {

                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.taxiform_record_history_adapter, parent, false);
                //int colorPos = position % colors.length;
               // convertView.setBackgroundColor(colors[colorPos]);

                viewHolder. formno = (TextView) convertView.findViewById(R.id.tv_formno_taxiadapter);
                viewHolder.  date = (TextView) convertView.findViewById(R.id.tv_date_taxiadapter);
                viewHolder.  id = (TextView) convertView.findViewById(R.id.tv_id_taxiadapter);
                viewHolder.  projecttype = (TextView) convertView.findViewById(R.id.tv_project_taxiadapter);
                viewHolder.  vihecleno = (TextView) convertView.findViewById(R.id.tv_vechicle_taxiadapter);
                viewHolder.  status = (ImageView) convertView.findViewById(R.id.iv_status_taxiadapter);
                viewHolder.  startkm = (TextView) convertView.findViewById(R.id.tv_startkm_taxiadapter);
                viewHolder.  endkm = (TextView) convertView.findViewById(R.id.tv_endkm_taxiadapter);

                convertView.setTag(viewHolder);

            }else{


                viewHolder = (ViewHolder) convertView.getTag();

            }



            getstatus = taxiFormData.getFlag()+"";
                  String fr = "<u>"+taxiFormData.getFormno()+"</u>";
            viewHolder.  formno.setText(Html.fromHtml(fr));
            viewHolder.  date.setText(taxiFormData.getSelectdate());
            viewHolder. id.setText(taxiFormData.getId()+"");
            viewHolder. projecttype.setText(taxiFormData.getProjecttype());
            viewHolder. vihecleno.setText(taxiFormData.getVechicleno());
            viewHolder.  startkm.setText(taxiFormData.getStartkm());
            viewHolder. endkm.setText(taxiFormData.getEndkm());


            if(getstatus.equals("1")){
                viewHolder. status.setBackgroundResource(R.drawable.success);
                //status.setText("Success");
            }else
            if(getstatus.equals("0")){
                viewHolder. status.setBackgroundResource(R.drawable.pending);
                //status.setText("Pending");
            }else if(getstatus.equals("2")){
                viewHolder. status.setBackgroundResource(R.drawable.upload);

                //status.setText("Retry");
            }




            viewHolder. status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    taxiFormData = searchlist.get(position);
                    getstatus = taxiFormData.getFlag()+"";

                   boolean b = getstatus.equals("2");

                   if(b){
                       new getDataAsnycTask(taxiFormData).execute(AppConstraint.TAXIFORMURL);
                       getActivity().startService(new Intent(getActivity(), SendLatiLongiServerIntentService.class));
                    }


                }
            });

            viewHolder.formno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    taxiFormData = searchlist.get(position);

                    Intent intent = new Intent(getActivity(), MapWebViewActivity.class);
                    intent.putExtra("formno",taxiFormData.getFormno());
                    startActivity(intent);



                }
            });




            return convertView;
        }

        public void filter(String charText) {
            // charText = charText.toLowerCase(Locale.getDefault());
            searchlist.clear();
            if (charText.length() == 0) {
                searchlist.addAll(taxiForm_DataArrayList);
            }
            else
            {
                for (TaxiFormData wp : taxiForm_DataArrayList)
                {
                    if (wp.getSelectdate().contains(charText))
                    {

                        searchlist.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }




    }

    public class getDataAsnycTask extends AsyncTask<String, Void, String> {
        TaxiFormData taxiFormData;

        getDataAsnycTask(TaxiFormData taxiFormData){
            this.taxiFormData = taxiFormData;

        }

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

            String s = HTTPPostRequestMethod.postMethodforESP(params[0], JSonobjParameter(taxiFormData));
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();

            // GPSTracker.isRunning= false;


            String re = s;
            try {

                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                String id = jsonObject.getString("id");


                if (status.equals("true")) {
                    int  flag = 1;
                    db.updatedetails(taxiFormData.getId(), taxiFormData.getSelectdate(),taxiFormData.getFormno(), taxiFormData.getProjecttype(),taxiFormData.getVechicleno(), taxiFormData.getStartkm(), taxiFormData.getStartkm_image(), taxiFormData.getEndkm(), taxiFormData.getEndkmimage(), flag);
                    //  db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    // db.deleteSingleRow_LatLong();

                    Toast.makeText(getActivity(), "Uploaded Successfully...", Toast.LENGTH_LONG).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(TaxiFormRecordFragment.this).attach(TaxiFormRecordFragment.this).commit();


                } else {

                    Toast.makeText(getActivity(), "internet is not working", Toast.LENGTH_LONG).show();
                    // flag = 0;
                    // db.updatedetails(keyid, edt_settaxiform_date.getText().toString(), edtproject_type.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    //  db.updatedetails(incri_id, edt_settaxiform_date.getText().toString(), form_no, edtproject_type.getText().toString(), edt_vehicle_no.getText().toString(), edtstartkmtext.getText().toString(), edt_startkmImage.getText().toString(), edtendkmtext.getText().toString(), edt_endkm_Image.getText().toString(), flag);
                    // db.deleteSingleRow_LatLong();







                }


            } catch (JSONException e) {
                Toast.makeText(getActivity(), "please check internet is not working", Toast.LENGTH_LONG).show();
            }


        }
    }

    public JSONObject JSonobjParameter(TaxiFormData taxiFormData) {
        JSONObject jsonObject = new JSONObject();
        try {

  /*    JSONArray jsonArrayParameter = new JSONArray();
         jsonArrayParameter.put("nov_2016");
         jsonArrayParameter.put("2016");
         jsonArrayParameter.put("11");

            List<TaxiFormData> da = db.getAllTaxiformData();
            JSONObject jsonObject2;
            for(int i =0;i<da.size();i++){
              jsonObject2 = new JSONObject();
                jsonObject2.put("in",da.get(i).getFormno());
                  jsonArrayParameter.put(jsonObject2);
            }
*/

            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("ftProject", taxiFormData.getProjecttype());
            jsonObject.put("fnStartkm", taxiFormData.getStartkm());
            jsonObject.put("ftStartkmImgUrl", taxiFormData.getStartkm_image());
            jsonObject.put("fnEndkm", taxiFormData.getEndkm());
            jsonObject.put("ftEndkmImgUrl", taxiFormData.getEndkmimage());
            jsonObject.put("Empid", empid);
            jsonObject.put("ftvehicleNo", taxiFormData.getVechicleno());
            jsonObject.put("ftTaxiFormNo", taxiFormData.getFormno());
            jsonObject.put("fdDate", taxiFormData.getSelectdate());


            // jsonObject.put("spName","USP_Get_Attendance");
            //  jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }


    /*private void setValue(){
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");
        data.add("6");

        ArrayList<ModelData> value = new ArrayList<>();

        for(int i =0; i< data.size();i++){
            name.add(data.get(i));
            id.add(data.get(i));

            ModelData data1 = new ModelData();
            data1.setName(name.get(i));
            value.add(data1);



        }
    }


    public class ModelData {
        String id;
        String name ;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }*/

}
