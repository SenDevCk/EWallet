package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;

import java.util.ArrayList;
import java.util.List;

//Not in use
public class TopupActivity extends AppCompatActivity {
    WebView webView;
    Toolbar toolbar_topup;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private boolean mSafeBrowsingIsInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup2);
        toolbar_topup = (Toolbar) findViewById(R.id.toolbar_topup);
        toolbar_topup.setTitle("Topup");
        setSupportActionBar(toolbar_topup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 24) {
            //Do not need to check the permission
            webView = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            /* WebViewClientImpl webViewClient = new WebViewClientImpl(this);
            webView.setWebViewClient(webViewClient);*/
            UserInfo2 userinfo = CommonPref.getUserDetails(TopupActivity.this);
            if (userinfo.getUserID().startsWith("2")) {
                webView.loadUrl("https://bihars.b2biz.co.in/BiharDiscom/");
                //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
            } else if (userinfo.getUserID().startsWith("1")) {
                webView.loadUrl("https://biharn.b2biz.co.in/NorthBiharDiscom/");
                //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
            }
        } else {
            if (checkAndRequestPermissions()) {
                webView = (WebView) findViewById(R.id.webview);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                UserInfo2 userinfo = CommonPref.getUserDetails(TopupActivity.this);
                if (userinfo.getUserID().startsWith("2")) {
                    webView.loadUrl("https://bihars.b2biz.co.in/BiharDiscom/");
                    //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
                } else if (userinfo.getUserID().startsWith("1")) {
                    webView.loadUrl("https://biharn.b2biz.co.in/NorthBiharDiscom/");
                    //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private boolean checkAndRequestPermissions() {

        int readphonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readphonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    webView = (WebView) findViewById(R.id.webview);
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    UserInfo2 userinfo = CommonPref.getUserDetails(TopupActivity.this);
                    if (userinfo.getUserID().startsWith("2")) {
                        webView.loadUrl("https://bihars.b2biz.co.in/BiharDiscom/");
                        //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
                    } else if (userinfo.getUserID().startsWith("1")) {
                        webView.loadUrl("https://biharn.b2biz.co.in/NorthBiharDiscom/");
                        //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
                    }
                } else {
                    //You did not accept the request can not use the functionality.
                    finish();
                }
                break;
        }
    }
}
