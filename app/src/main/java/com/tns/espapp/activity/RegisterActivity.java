package com.tns.espapp.activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tns.espapp.R;
import com.tns.espapp.push_notification.MyFirebaseInstanceIDService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.your_full_name) EditText edt_name;
    @InjectView(R.id.btn_register) Button btn_register;
    @InjectView(R.id.back_register) ImageView back_register;
    @InjectView(R.id.reg_token_no) EditText reg_token_no;
    @InjectView(R.id.reg_get_emino) EditText reg_emino;


    Toolbar  toolbar;
    TextView   tv_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);


        // setSupportActionBar(toolbar);
        // getSupportActionBar().setTitle("");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);


        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();
        reg_emino.setText(device_id);

        String Token = MyFirebaseInstanceIDService.SharedSave.getInstance(getApplicationContext()).getDeviceToken();
        reg_token_no.setText(Token);

        Log.d("token no",Token);


    }
    @OnClick( {R.id.back_register})
    public void back(){
        finish();

    }



    @OnClick({ R.id.btn_register})
    public void commonMethod(Button button) {
       if(button.getText().toString().equals("Sign Up")){

           Toast.makeText(getApplicationContext(),button.getText().toString(),Toast.LENGTH_LONG).show();
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if(item.getItemId() == android.R.id.home){

            finish();
        }

        return true;

    }
}
