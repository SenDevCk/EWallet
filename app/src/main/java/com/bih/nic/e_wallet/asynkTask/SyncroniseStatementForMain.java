package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.activities.StatmentSyncronizeActivity;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

public class SyncroniseStatementForMain extends AsyncTask<String, Void, ArrayList<Statement>> {

    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    private Activity activity;
    public SyncroniseStatementForMain(Activity activity){
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }
    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Wait Syncing...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<Statement> doInBackground(String... strings) {
        ArrayList<Statement> statementMS = null;
        try {
            String res = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                res = WebHandler.callByPostwithoutparameter( Urls_this_pro.MAKE_SYNC_URL + reqString(String.valueOf(strings[0])));
            }else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            statementMS = WebServiceHelper.SynkParser(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return statementMS;
    }

    @Override
    protected void onPostExecute(ArrayList<Statement> statementMS) {
        super.onPostExecute(statementMS);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (statementMS !=null) {
            long c=new DataBaseHelper(activity).saveTotalStatement(statementMS,CommonPref.getUserDetails(activity).getUserID());
            if (c>0){
                CommonPref.setCurrentDateForSync(activity,Utiilties.getCurrentDateWithTime());
            }
        }else{
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();

        }
        DataBaseHelper db = new DataBaseHelper(activity);
        if (db.getPendingCount(CommonPref.getUserDetails(activity).getUserID()) > 0) {
            Intent intent = new Intent(activity, StatmentSyncronizeActivity.class);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(activity, "Syncronise Service Cancelled !", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
}