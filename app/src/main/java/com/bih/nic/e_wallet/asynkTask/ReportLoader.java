package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.activities.ReportActivity;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

import static com.bih.nic.e_wallet.utilitties.CommonPref.neftEntities;

public class ReportLoader extends AsyncTask<String, Void, String> {
    Activity activity;
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
    public ReportLoader(Activity activity) {
        this.activity=activity;
        dialog1= new ProgressDialog(this.activity);
        alertDialog=new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter(Urls_this_pro.GET_DCR_REPORT + reqString(String.valueOf(strings[0])));
            }else{
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }else{
            Toast.makeText(activity, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (dialog1.isShowing())dialog1.cancel();
        if (s!=null){
            GlobalVariables.reportEntities= WebServiceHelper.getReport(s);
            if ( GlobalVariables.reportEntities.size()>0){
                Intent intent=new Intent(activity, ReportActivity.class);
                activity.startActivity(intent);
            }
            else {
                Toast.makeText(activity, "NO DATA FOUND !", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(activity, "NO DATA FOUND ! DUE TO SERVER PROBLEM !", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (dialog1.isShowing())dialog1.cancel();
        Toast.makeText(activity, "NEFT Service Canceled !", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        if (dialog1.isShowing())dialog1.cancel();
        Toast.makeText(activity, ""+s, Toast.LENGTH_SHORT).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP );//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
}
