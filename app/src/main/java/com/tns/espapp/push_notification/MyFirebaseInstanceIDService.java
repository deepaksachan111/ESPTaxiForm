package com.tns.espapp.push_notification;

/**
 * Created by TNS on 15-May-17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static final String TOKEN_BROADCAST_ACTION = "com.tns.espapp";

    Intent intent;

    @Override
    public void onTokenRefresh() {

        intent = new Intent(TOKEN_BROADCAST_ACTION);
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
        intent.putExtra("TOKEN", refreshedToken);
        sendBroadcast(intent);

    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later

        SharedSave.getInstance(getApplicationContext()).saveDeviceToken(token);

    }

    public static class SharedSave{

        private static final String SHARED_PREF_NAME = "FCMSharedPref";
        private static final String TAG_TOKEN = "tagtoken";

        private static SharedSave mInstance;
        private static Context mCtx;

        private SharedSave(Context context) {
            mCtx = context;
        }

        public static synchronized SharedSave getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new SharedSave(context);
            }
            return mInstance;
        }

        //this method will save the device token to shared preferences
        public boolean saveDeviceToken(String token){
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TAG_TOKEN, token);
            editor.apply();
            return true;
        }

        public  String getDeviceToken(){
            SharedPreferences preferences= mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return preferences.getString(TAG_TOKEN,"");

        }

    }

}