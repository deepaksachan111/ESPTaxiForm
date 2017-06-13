package com.tns.espapp.activity;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tns.espapp.R;
import com.tns.espapp.database.DatabaseHandler;
import com.tns.espapp.database.NotificationData;

import java.util.Comparator;
import java.util.List;

public class ReadNotificationActivity extends AppCompatActivity {

    DatabaseHandler  db;
    MyCustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);
        db = new DatabaseHandler(this);


        /*
        ImageView iv = (ImageView)findViewById(R.id.iv_notification_back) ;
        iv.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                finish();
            }


        });

       */



        TextView tv_set_notifu =(TextView)findViewById(R.id.tv_set_notifu);
        ListView listView =(ListView)findViewById(R.id.lst_notification) ;

        List<NotificationData> getdata = db.getAllNotification();


        adapter = new MyCustomAdapter(getApplicationContext(),R.layout.notificationmessageui,getdata);
     /*   adapter.sort(new Comparator<NotificationData>() {
            public int compare(NotificationData object1, NotificationData object2) {
                return object1.getNoti_tittle().compareToIgnoreCase(object2.getNoti_tittle());
            }


        });*/
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(getdata.size()>0)
        {
            for(NotificationData data : getdata){

               // Toast.makeText(getApplicationContext(),data.getNoti_message(),Toast.LENGTH_LONG).show();
               // tv_set_notifu.append(data.getNoti_message()+"\n");
            }




        }




    }


    private class MyCustomAdapter extends ArrayAdapter {
        Animation scaleUp;
        List <NotificationData> notificationDataArraylist;
        public MyCustomAdapter(@NonNull Context context, @LayoutRes int resource,List<NotificationData> datas) {
            super(context, resource, datas);
            this.notificationDataArraylist =datas;
            scaleUp = AnimationUtils.loadAnimation(context, R.anim.zoon_in);
        }


        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

           ViewHolder viewHolder = new ViewHolder();

                if (convertView == null) {

                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationmessageui, null);
                    viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_notification_message);
                    viewHolder. time = (TextView)convertView.findViewById(R.id.tv_notification_time);
                    viewHolder. iv_delete=(ImageView)convertView.findViewById(R.id.iv_notification_delete);
                    convertView.setTag(viewHolder);

                } else {
                    viewHolder = (ViewHolder) convertView.getTag();

                }


                final NotificationData data = notificationDataArraylist.get(position);

            //  viewHolder.textView.setAnimation(scaleUp);

              viewHolder.textView.setText(data.getNoti_message());
              viewHolder.time.setText(data.getNoti_tittle());
              viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.deleteSingleRow(data.getNoti_message());

                    notificationDataArraylist.remove(data.getNoti_message());

                    adapter.remove(adapter.getItem(position));
                    adapter.notifyDataSetChanged();
                }
            });



            return convertView;

        }
        private class ViewHolder {
            TextView textView;
            TextView time;
            ImageView iv_delete;


        }

    }

}
