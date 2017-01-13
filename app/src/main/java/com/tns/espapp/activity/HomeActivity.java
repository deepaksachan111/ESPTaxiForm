package com.tns.espapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tns.espapp.R;
import com.tns.espapp.fragment.HomeFragment;
import com.tns.espapp.fragment.TaxiFormFragment;
import com.tns.espapp.service.GPSTracker;
import com.tns.espapp.service.GetCurrentGPSLocation;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawerPane, linear_taxiform;
    private Toolbar toolbar;
    private TextView tv_taxiform, tv_userhomeid;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        navigationdrawer();
        findIDS();
       // mDrawerLayout.closeDrawer(mDrawerPane);
       // mDrawerLayout.openDrawer(mDrawerPane);


        if(savedInstanceState == null){

           // getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_home_frag,HomeFragment.newInstance(23)).commit();

            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_home_frag,new TaxiFormFragment()).commit();

        }

       SharedPreferences preferences = getSharedPreferences("ID",Context.MODE_PRIVATE);

        tv_userhomeid.setText(preferences.getString("empid",""));


    }

    private void findIDS(){
        tv_taxiform =(TextView)findViewById(R.id.tv_taxiform_homeactivity);
        tv_userhomeid =(TextView)findViewById(R.id.tv_userhome_id);
        linear_taxiform =(LinearLayout) findViewById(R.id.linear_taxiform_homeactivity);
        tv_taxiform.setOnClickListener(this);
        linear_taxiform.setOnClickListener(this);


    }



    private void navigationdrawer(){
        mDrawerPane = (LinearLayout) findViewById(R.id.drawerPane);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
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

        if(v == tv_taxiform ){

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_home_frag,new TaxiFormFragment()).addToBackStack(null).commit();
            mDrawerLayout.closeDrawer(mDrawerPane);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}
