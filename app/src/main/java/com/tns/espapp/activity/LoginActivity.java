package com.tns.espapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.otto.Subscribe;
import com.tns.espapp.AllPermissionGrant;
import com.tns.espapp.AppConstraint;
import com.tns.espapp.HTTPPostRequestMethod;
import com.tns.espapp.LocataionData;
import com.tns.espapp.LocationAddress;
import com.tns.espapp.R;
import com.tns.espapp.service.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    String[] permissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    public static final int MULTIPLE_PERMISSIONS = 10;
    Button btn_Login;
    private EditText edt_login, edt_password;
    private String strUserName, strUserPassword;
    private CheckBox chk_remember;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences_setid;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_id;
    boolean b_savecheck;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findIDS();
/*

        if(shouldAskPermissions()){
            askPermissions();
        }

*/

        if(checkPermissions()){

        }

        sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        sharedPreferences_setid = getSharedPreferences("ID", Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
        editor_id = sharedPreferences_setid.edit();

        edt_password.setTypeface(Typeface.DEFAULT);
        edt_password.setTransformationMethod(new PasswordTransformationMethod());

        b_savecheck = sharedPreferences.getBoolean("saveLogin", false);

        if (b_savecheck) {
            edt_login.setText(sharedPreferences.getString("username", ""));
            edt_password.setText(sharedPreferences.getString("password", ""));


            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }


    }

    private void findIDS() {
        btn_Login = (Button) findViewById(R.id.btn_login);
        edt_login = (EditText) findViewById(R.id.input_email);
        edt_password = (EditText) findViewById(R.id.input_password);
        chk_remember = (CheckBox) findViewById(R.id.ch_rememberme);
        btn_Login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.btn_login: {


                strUserName = edt_login.getText().toString().trim();
                strUserPassword = edt_password.getText().toString().trim();

                if (chk_remember.isChecked()) {

                    editor.putBoolean("saveLogin", true);
                    editor.putString("username", strUserName);
                    editor.putString("password", strUserPassword);
                    editor.commit();
                    InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_login.getWindowToken(), 0);
                } else {
                    editor.clear();
                    editor.commit();

                }

                if (TextUtils.isEmpty(strUserName)) {
                    edt_login.setError("Please Enter LoginID");
                    return;
                } else if (TextUtils.isEmpty(strUserPassword)) {
                    edt_password.setError("Please Enter Password");
                    return;

                } else

                {

                    btn_Login.setEnabled(true);
                    editor_id.clear();
                    editor_id.commit();
                    new getDataAsnycTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, AppConstraint.ESP_LOGIN);

                }


                // startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                break;
            }
        }
    }


    private class getDataAsnycTask extends AsyncTask<String, Void, String> {
        // ProgressDialog pd = AllPermissionGrant.createProgressDialog(LoginActivity.this);
        ProgressDialog pd = new ProgressDialog(LoginActivity.this, R.style.AppCompatAlertDialogStyle);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("get credentials");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String s = HTTPPostRequestMethod.postMethodforESP(params[0], JSonobjParameter(strUserName, strUserPassword));
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pasreResultDesplay(s);
            pd.dismiss();

            // startActivity(new Intent(getApplicationContext(),HomeActivity.class));

        }
    }

    private void pasreResultDesplay(String res) {
        String status = "";
        String empid = "";
        try {
            JSONObject jsonObject = new JSONObject(res);
            // JSONArray jsonArray = new JSONArray(res);

            //  for(int i = 0; i<jsonArray.length(); i++){
            //
            status = jsonObject.getString("status");
            empid = jsonObject.getString("empid");


            if (status.equals("1")) {

                editor_id.putString("empid", empid);
                editor_id.commit();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                //  Toast.makeText(getApplicationContext(),"Welcome : "+empid,Toast.LENGTH_LONG).show();
            } else {
                btn_Login.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Invalid Id or Password", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {

            /// startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            Toast.makeText(getApplicationContext(), "Internet is not working", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    private JSONObject JSonobjParameter(String loginname, String password) {
        JSONObject jsonObject = new JSONObject();
        try {

       /* JSONArray jsonArrayParameter = new JSONArray();
         jsonArrayParameter.put(loginname);
         jsonArrayParameter.put(password);*/


            //jsonObject.put( "SqlQuery","select top 1 * from tbl_opreg");
            jsonObject.put("DatabaseName", "TNS_HR");
            jsonObject.put("ServerName", "bkp-server");
            jsonObject.put("UserId", "sanjay");
            jsonObject.put("Password", "tnssoft");
            jsonObject.put("spName", "USP_login");
            jsonObject.put("loginID", loginname);
            jsonObject.put("loginPassword", password);

            // jsonObject.put("ParameterList",jsonArrayParameter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


  /*  public boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    public void askPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }*/


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // no permissions granted.
                    Toast.makeText(this, "permission not Granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }




}
