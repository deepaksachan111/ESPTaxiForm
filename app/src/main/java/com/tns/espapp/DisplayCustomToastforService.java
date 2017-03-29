package com.tns.espapp;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by TNS on 28-Mar-17.
 */

public class DisplayCustomToastforService  implements Runnable{
      Context context;
      String text;

    public DisplayCustomToastforService(Context c, String t){
        context = c;
        text = t;
    }


    @Override
    public void run() {
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
}
