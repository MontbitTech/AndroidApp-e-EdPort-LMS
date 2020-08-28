package com.example.lmsandroidapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lmsandroidapplication.SchoolInformation.SchoolInformationActivity;
import com.example.lmsandroidapplication.Utilities.PrefManager;
import com.google.android.material.navigation.NavigationView;

public class LMSWebViewActivity extends AppCompatActivity {
    private  String PAGE_URL;
    private WebView webView;
    LinearLayout layout_error  ;
    DrawerLayout drawerLayout;
    Button btn_retry ;
    ProgressBar progress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l_m_s_web_view);

       PAGE_URL= new PrefManager(this).getSchoolUrl();



        layout_error = findViewById(R.id.error_layout);
        btn_retry = findViewById(R.id.btn_retry);

        progress = findViewById(R.id.progress);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadWebViewUrl(PAGE_URL);
            }
        });
        CookieSyncManager.createInstance(this);

        initWebView();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialog.setMessage("This option will allow you to change your current school.\nDo you wish to proceed? ");
        alertDialog.setIcon(R.drawable.alerticon);
        alertDialog.setTitle("Change Schools");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new PrefManager(getApplicationContext()).setSchoolUrl(null);
                startSchoolInformationActivity();
                finish();

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = alertDialog.create();

        NavigationView view = findViewById(R.id.nav_view);
        drawerLayout= findViewById(R.id.drawer_layout);

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if(id==R.id.nav_change_school){
                    alert.show();

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }
    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;

        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString().replace("; wv",""));
            webView.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //Toast.makeText(SchoolActivity.this,"Something Wrong",Toast.LENGTH_SHORT).show();
            webView.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
        }
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);

            //Toast.makeText(SchoolActivity.this,"Something Wrong",Toast.LENGTH_SHORT).show();
            webView.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();


            if( URLUtil.isNetworkUrl(url) ) {

                if(url.contains("https://meet.google.com")){
                    webView.setWebViewClient(null);
                    webView.loadUrl(url);
                }
                return false;
            }
            if(appInstalledOrNot(url)){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
            }
            else {
                Toast.makeText(getApplicationContext(),"Please install gmeet",Toast.LENGTH_LONG).show();

            }

            return true;

        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        //************CONFIGURING BACK BUTTON FOR THE DRAWABLE LAYOUT *****************//

        if(keyCode==KeyEvent.KEYCODE_BACK && drawerLayout.isDrawerOpen(GravityCompat.START) ){

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        }


        else if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isCanGoBack();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView =  findViewById(R.id.web);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);


        LoadWebViewUrl(PAGE_URL);
    }

    private void LoadWebViewUrl(String url) {
        if (isInternetConnected()) {
            webView.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            webView.loadUrl(url);
        } else {
            webView.setVisibility(View.GONE);
            layout_error.setVisibility(View.VISIBLE);
        }
    }
    private void isCanGoBack() {
        if (webView.canGoBack())
            webView.goBack();
        else
            finish();
    }
    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    void startSchoolInformationActivity(){
        //#todo

        startActivity(new Intent(LMSWebViewActivity.this, SchoolInformationActivity.class));
    }

    private boolean appInstalledOrNot(String uri) {

        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

}


