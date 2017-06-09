package com.tns.espapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.tns.espapp.R;

public class MapWebViewActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{
    private  WebView webView;
    private  SwipeRefreshLayout swipeRefreshLayout;
    private  String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_web_view);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);


    /*     ImageView iv = (ImageView)findViewById(R.id.back_mapview) ;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

*/

        webView = (WebView) findViewById(R.id.webView1);

        String s = getIntent().getStringExtra("formno");

         url ="http://tnssofts.com/PMSNT/EmployeeExpence/graph?formid="+s;
        startWebView(url);

    }


    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                // progressDialog.dismiss();
                return true;

            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(MapWebViewActivity.this);
                    progressDialog.getWindow().setGravity(Gravity.CENTER);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        // progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options
	    /*
	    webView.getSettings().setLoadWithOverviewMode(true);
	    webView.getSettings().setUseWideViewPort(true);
	    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    webView.setScrollbarFadingEnabled(false);
	    webView.getSettings().setBuiltInZoomControls(true);
	    */

	    /*
	     String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
	     */

        //Load url in webview
        webView.loadUrl(url);


    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        finish();
        startActivity(getIntent());

    }
}
