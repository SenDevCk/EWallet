package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.bih.nic.e_wallet.activities.NeftDateWiseReportActivity;

public class NeftDatewiseService extends AsyncTask<String,Void,String> {
   Activity activity;
    public NeftDatewiseService(Activity activity) {
        this.activity=activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


    }
}
