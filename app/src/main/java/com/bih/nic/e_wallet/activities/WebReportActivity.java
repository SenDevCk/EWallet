package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.WebViewClientImpl;

import java.util.ArrayList;
import java.util.List;

public class WebReportActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 10;
    private WebView webView;
    UserInfo2 userInfo2;
    Toolbar toolbar_web_rep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_report);
        toolbar_web_rep = (Toolbar) findViewById(R.id.toolbar_web_rep);
        toolbar_web_rep.setTitle("Web Report");
        setSupportActionBar(toolbar_web_rep);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        userInfo2 = CommonPref.getUserDetails(WebReportActivity.this);
        webView = (WebView) findViewById(R.id.webview);
        //setUpWebView();
    }

    private void setUpWebView() {
       WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        //webView.setWebViewClient(webViewClient);

        webView.loadUrl("http://energybills.bsphcl.co.in/WebApplication1/newjsp.jsp?walletId=" + userInfo2.getWalletId().trim());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 24) {
            //Do not need to check the permission
            setUpWebView();

        } else {
            if (checkAndRequestPermissions()) {
                setUpWebView();

            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
                    setUpWebView();
                } else {
                    //You did not accept the request can not use the functionality.
                    finish();
                }
                break;
        }
    }
}