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

import com.bih.nic.e_wallet.activities.StatmentSyncronizeActivity;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.GrivanceEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.interfaces.GrivanceListBinder;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

public class GrievanceListService extends AsyncTask<String, Void, ArrayList<GrivanceEntity>> {
    static GrivanceListBinder grivanceListBinder;
    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    private Activity activity;
    public GrievanceListService(Activity activity){
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }
    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Wait Loading...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<GrivanceEntity> doInBackground(String... strings) {
        ArrayList<GrivanceEntity> statementMS = null;
        try {
            String res = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                res = WebHandler.callByPostwithoutparameter( Urls_this_pro.GET_COMPLAIN_LiST + reqString(String.valueOf(strings[0])));
            }else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            statementMS = WebServiceHelper.GrievanceParser(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return statementMS;
    }

    @Override
    protected void onPostExecute(ArrayList<GrivanceEntity> grivanceEntities) {
        super.onPostExecute(grivanceEntities);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (grivanceEntities !=null) {
           if (grivanceEntities.size()>0){
               grivanceListBinder.grivanceFound(grivanceEntities);
           }else{
               grivanceListBinder.grivanceNotFound("No Data Found");
           }
        }else{
            grivanceListBinder.grivanceNotFound("Server Problem !");
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }

    public static void grievanceListBinderMethod(GrivanceListBinder grivanceListBinder1){
        grivanceListBinder=grivanceListBinder1;
    }
}