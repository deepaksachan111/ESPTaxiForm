package com.tns.espapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.NotificationData;

import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

      DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {


            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {

                String tittle = bundle.get("tittle") + "";
                String messages = bundle.get("message") + "";
                String image = bundle.get("image") + "";
                db.add_DB_Notification(new NotificationData(tittle,messages,image,0));
                Set<String> keys = bundle.keySet();

                for (String a : keys) {
                 /*   String tittle = bundle.get("tittle") + "";
                    String messages = bundle.get("message") + "";
                    String image = bundle.get("image") + "";*/
                  // db.add_DB_Notification(new NotificationData(tittle,messages,image,0));
                    Log.e("for Bundle", "[" + a + "=" + bundle.get(a) + "]");

                }

            }


            }

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            }
        }, 3000);



    }
    }