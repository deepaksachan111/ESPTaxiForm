package com.tns.espapp;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by TNS on 12/22/2016.
 */

public class HTTPPostRequestMethod {



    public static String postMethodforESP(String url, JSONObject jsonObject) {

        String responce = "";

        HttpResponse httpResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Content-type", "application/json");
        // List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
      //  HttpParams params = httpClient.getParams();
       // HttpConnectionParams.setConnectionTimeout(params, 10000);
       // HttpConnectionParams.setSoTimeout(params, 10000);



        StringEntity se = null;
        try {
            se = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        se.setContentEncoding((new BasicHeader(HTTP.CONTENT_TYPE, "application/json")));
        httpPost.setEntity(se);





        try {
            httpResponse = httpClient.execute(httpPost);
            int stat = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();
            responce = EntityUtils.toString(httpEntity);
            // write response to log
            Log.d("Http Post Response:", responce.toString());
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }

        return responce;
    }






}
