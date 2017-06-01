package com.tns.espapp.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tns.espapp.R;
import com.tns.espapp.fragment.FeedBackFragment;
import com.tns.espapp.fragment.FeedbackFragmentHistory;
import com.tns.espapp.fragment.LocationHistoryFragment;
import com.tns.espapp.RouteMapsActivity;
import com.tns.espapp.fragment.PernsonalInfoFragment;
import com.tns.espapp.fragment.TaxiFormFragment;
import com.tns.espapp.fragment.TaxiFormRecordFragment;
import com.tns.espapp.service.SendLatiLongiServerIntentService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.tns.espapp.fragment.TaxiFormFragment.decodeBase64;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    final int CROP_PIC = 2;
    private Uri picUri;

    public static final String MyPREFERENCES = "MyPre" ;//file name
    public static final String  key = "nameKey";
    SharedPreferences sharedPreferences;
    Bitmap btMap;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;


    String[] permissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    public static final int MULTIPLE_PERMISSIONS = 10;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private TextView tv_taxiform, tv_userhomeid, tv_location_history,getTv_taxiform_record ,tv_toolbar, tvpersomalinfo,tv_feedback, tv_feedback_history,tv_locationmap,tv_notification;
    private  LinearLayout linear_taxiform,mDrawerPane;
    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

       // setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        tv_toolbar =(TextView)toolbar. findViewById(R.id.tv_toolbar);

        navigationdrawer();
        findIDS();
       startService(new Intent(getApplication(), SendLatiLongiServerIntentService.class));
        // mDrawerLayout.closeDrawer(mDrawerPane);
        // mDrawerLayout.openDrawer(mDrawerPane);


        if (savedInstanceState == null) {

            // getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_home_frag,HomeFragment.newInstance(23)).commit();

            if(checkPermissions()){
            }


       /*     android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left,android. R.anim.slide_out_right);
            ft.replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).commit();*/

            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_home_frag, new TaxiFormFragment()).commit();

        }

        SharedPreferences preferences = getSharedPreferences("ID", Context.MODE_PRIVATE);

        tv_userhomeid.setText(preferences.getString("empid", ""));

        setProfileImg();

      /*  Fragment fragment_obj = (Fragment) getSupportFragmentManager().findFragmentById(R.id.taxifragment);

        TextView tv= (TextView)fragment_obj.getView().findViewById(R.id.tv_form_no);*/
        //tv.setText("deepak");


    }




    private void findIDS() {
        tv_taxiform = (TextView) findViewById(R.id.tv_taxiform_homeactivity);
        tv_userhomeid = (TextView) findViewById(R.id.tv_userhome_id);
        linear_taxiform = (LinearLayout) findViewById(R.id.linear_taxiform_homeactivity);
        tv_location_history = (TextView) findViewById(R.id.location_history);
        tvpersomalinfo =(TextView)findViewById(R.id.tv_personal_info) ;
        getTv_taxiform_record=(TextView)findViewById(R.id.taxiformrecord_history_home) ;
        tv_feedback=(TextView)findViewById(R.id.tv_feedback);
        tv_feedback_history=(TextView)findViewById(R.id.tv_feedback_history);
        tv_locationmap =(TextView)findViewById(R.id.tv_currentlocation);
        tv_notification=(TextView)findViewById(R.id.tv_notification);
        tv_taxiform.setOnClickListener(this);
        linear_taxiform.setOnClickListener(this);
        tv_location_history.setOnClickListener(this);
        tvpersomalinfo.setOnClickListener(this);
        getTv_taxiform_record.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);
        tv_feedback_history.setOnClickListener(this);
        tv_locationmap.setOnClickListener(this);
        tv_notification.setOnClickListener(this);
    }


    private void navigationdrawer() {
        mDrawerPane = (LinearLayout) findViewById(R.id.drawerPane);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,
                //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility


        );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public void onClick(View v) {

        if (v == tv_taxiform) {
            tv_toolbar.setText("Taxi" + "Form");
/*
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left,android. R.anim.slide_out_right);

            ft.replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).commit();*/
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new TaxiFormFragment()).addToBackStack(null).commit();
            mDrawerLayout.closeDrawer(mDrawerPane);

        } else

            if (v == tv_location_history) {
                tv_toolbar.setText("Location History");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new LocationHistoryFragment()).addToBackStack(null).commit();
                mDrawerLayout.closeDrawer(mDrawerPane);
            }

        else
            if (v == tvpersomalinfo) {
                tv_toolbar.setText("Personal Info");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new PernsonalInfoFragment()).addToBackStack(null).commit();
                mDrawerLayout.closeDrawer(mDrawerPane);

               // startActivity(new Intent(getApplicationContext(), RouteMapsActivity.class));
            }

         else if (v == getTv_taxiform_record) {
            tv_toolbar.setText("TaxiForm History");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new TaxiFormRecordFragment()).addToBackStack(null).commit();
            mDrawerLayout.closeDrawer(mDrawerPane);


        }
            else if (v == tv_feedback) {
                tv_toolbar.setText("FeedBack");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedBackFragment()).addToBackStack(null).commit();
                mDrawerLayout.closeDrawer(mDrawerPane);

            }
            else if (v == tv_feedback_history) {
                tv_toolbar.setText("FeedBack History");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag, new FeedbackFragmentHistory()).addToBackStack(null).commit();
                mDrawerLayout.closeDrawer(mDrawerPane);

            }  else
            if (v == tv_locationmap) {
                tv_toolbar.setText("Current Location");

                startActivity(new Intent(getApplicationContext(), RouteMapsActivity.class));
                mDrawerLayout.closeDrawer(mDrawerPane);
            }
        if (v == tv_notification) {
            tv_toolbar.setText("Notification");

            startActivity(new Intent(getApplicationContext(), ReadNotificationActivity.class));
            mDrawerLayout.closeDrawer(mDrawerPane);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {


// dont call **super**, if u want disable back button in current screen.
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            // super.onBackPressed();
            //  getFragmentManager().popBackStackImmediate();


        } else {
            alertdiaologbackbutton();
            // super.onBackPressed();
            // }
        }
    }


    public void alertdiaologbackbutton() {


        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press back again to close this app", 4000);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
             //tv_toolbar.setText("ESP");
            // mDrawerLayout.openDrawer(mDrawerPane);
        } else {
            if (toast != null) {
                toast.cancel();

            }


         /*   mDrawerLayout.closeDrawer(mDrawerPane);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/

            finish();
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // no permissions granted.
                    Toast.makeText(this, "permission not Granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }


    }


    private void setProfileImg(){
        imageView = (ImageView) findViewById(R.id.profile_image);

       sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)){
            String u = sharedPreferences.getString(key,"");
            btMap = decodeBase64(u);

            imageView.setImageBitmap(btMap);
        }

        ImageView btnLoadImage = (ImageView) findViewById(R.id.img_edt);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,RESULT_LOAD_IMAGE);
            } catch (ActivityNotFoundException anfe) {
                Toast toast = Toast.makeText(HomeActivity.this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                toast.show();
            }


            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
           // Uri selectedImage = data.getData();

            picUri = data.getData();
            performCrop();

          /*  String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            btMap = BitmapFactory.decodeFile(picturePath);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, encodeTobase64(btMap));
            editor.commit();
*/

        }else if (requestCode == CROP_PIC) {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            btMap= extras.getParcelable("data");
            imageView.setImageBitmap(btMap);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, encodeTobase64(btMap));
            editor.commit();
        }





    }
    public static String encodeTobase64(Bitmap image){
        Bitmap image1 = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image1.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b =baos.toByteArray();
        String imageEncode = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("image log", imageEncode);
        return imageEncode;
    }
    public static Bitmap decodeBase64(String input){
        byte[] decodeByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop

            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 250);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}