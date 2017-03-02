package com.tns.espapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

/**
 * Created by TNS on 1/16/2017.
 */

public class GetAllAsyncTask extends AsyncTask<String,Void,String> {
    ArrayList<String> nameArray,idArray;
    String name, id,response,responseCode;
    ArrayAdapter adapter;
    Spinner spinner;
    Activity activity;
    ArrayList<String> customListArray;
    String url ;

    public GetAllAsyncTask(Activity activity, ArrayList<String> typeList,ArrayList<String> idList,ArrayList<String> customListArray, ArrayAdapter adapter, Spinner spinner, String url){
        this.activity = activity;
        nameArray = typeList;
        idArray = idList;
        this.adapter = adapter;
        this.spinner = spinner;
        this.customListArray = customListArray;
        this.url = url;

    }

    @Override
    protected void onPreExecute() {
        customListArray.clear();
        nameArray.clear();
        idArray.clear();

    }

    @Override
    protected String doInBackground(String... strings) {




        return null;
    }

    @Override
    protected void onPostExecute(String s) {

    }
}

