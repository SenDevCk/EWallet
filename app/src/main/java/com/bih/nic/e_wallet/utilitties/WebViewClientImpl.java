package com.bih.nic.e_wallet.utilitties;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bih.nic.e_wallet.entity.UserInfo2;

/**
 * Created by NIC2 on 5/10/2018.
 */

public class WebViewClientImpl extends WebViewClient {

    private Activity activity = null;

    public WebViewClientImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        UserInfo2 userInfo2 = CommonPref.getUserDetails(activity);
        if (url.indexOf("http://energybills.bsphcl.co.in/WebApplication1/newjsp.jsp?walletId=" + userInfo2.getWalletId().trim()) > -1)
            return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

}