package com.bih.nic.e_wallet.asynkTask;

import android.annotation.TargetApi;
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

import com.bih.nic.e_wallet.activities.GetNeftPayActivity;
import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.interfaces.BalanceListner;
import com.bih.nic.e_wallet.interfaces.NeftListener;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

public class GetNEFTService extends AsyncTask<String,Void,String> {
    Activity activity;
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
    String from, to, uid;
    public GetNEFTService(Activity activity,String from,String to,String uid){
        this.activity=activity;
        this.from=from;
        this.to=to;
        this.uid=uid;
        dialog1= new ProgressDialog(this.activity);
        alertDialog=new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog1.setMessage("Loading...");
        dialog1.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter( Urls_this_pro.GET_NEFT_PAY + reqString(String.valueOf(strings[0])));
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
            ArrayList<NeftEntity> neftEntities=WebServiceHelper.getNeft(s);
            if (neftEntities.size()>0){
                long in_no=new DataBaseHelper(activity).insertNeftDetails(neftEntities,CommonPref.getUserDetails(activity).getUserID());
                }
                else {
                Toast.makeText(activity, "NO DATA FOUND !", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(activity, "NO DATA FOUND ! DUE TO SERVER PROBLEM !", Toast.LENGTH_SHORT).show();
        }
        ArrayList<NeftEntity> c= new DataBaseHelper(activity).getTotalNeft(from,to,uid);

        if (c.size()>0){
            CommonPref.neftEntities=c;
            Intent intent=new Intent(activity,GetNeftPayActivity.class);
            activity.startActivity(intent);
        }else {
            Toast.makeText(activity, "NO DATA FOUND !", Toast.LENGTH_SHORT).show();
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
