package com.tns.espapp.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.tns.espapp.R;
import com.tns.espapp.activity.MapWebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapWebViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  String url;
    public MapWebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_map_web_view, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)v. findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);


        Toolbar toolbar =(Toolbar)getActivity().findViewById(R.id.toolbar);
         TextView tv =(TextView) toolbar. findViewById(R.id.tv_toolbar);
        tv.setText("TaxiMap");


        webView = (WebView)v. findViewById(R.id.webView1);

        String s = getArguments().getString("YourKey");

        url ="http://tnssofts.com/PMSNT/EmployeeExpence/graph?formid="+s;
        startWebView(url);



        return v;
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
                    progressDialog = new ProgressDialog(getActivity());
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

    @Override
    public void onRefresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(MapWebViewFragment.this).attach(MapWebViewFragment.this).commit();

    }
}
