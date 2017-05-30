package com.tns.espapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.NotificationData;

import java.util.List;

public class ReadNotificationActivity extends AppCompatActivity {
      DatabaseHandler  db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);
        db = new DatabaseHandler(this);

        TextView tv_set_notifu =(TextView)findViewById(R.id.tv_set_notifu);

        List<NotificationData> getdata = db.getAllNotification();

        if(getdata.size()>0)
        {
            for(NotificationData data : getdata){

               // Toast.makeText(getApplicationContext(),data.getNoti_message(),Toast.LENGTH_LONG).show();
                tv_set_notifu.append(data.getNoti_message()+"\n");
            }


        }



    }
}
