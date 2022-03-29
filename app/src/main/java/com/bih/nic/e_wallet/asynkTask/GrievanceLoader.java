package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.interfaces.BalanceListner;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

import org.json.JSONObject;

public class GrievanceLoader extends AsyncTask<String, Void, String> {
    static BalanceListner balanceListner;
    private Activity activity;
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;

    public GrievanceLoader(Activity activity) {
        this.activity = activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        dialog1.setMessage("Loading...");
        dialog1.setCancelable(false);
        dialog1.show();
    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.accumulate("encryptedData",reqString(strings[0]));
                    jsonObject.accumulate("plainData1",strings[1]);
                    jsonObject.accumulate("plainData2",strings[2]);
                    jsonObject.accumulate("plainData3","NA");
                    jsonObject.accumulate("plainData4","NA");
                    jsonObject.accumulate("plainData5","NA");
                    jsonObject.accumulate("plainData6","NA");
                    result = WebHandler.callByPost(jsonObject.toString(),Urls_this_pro.GET_COMPLAIN.trim());
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(activity, "Your device must have atleast Kitkat or Above Version", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("MSG_STR").trim().contains("SUCCESS")){
                    Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }else{
                    Toast.makeText(activity, ""+jsonObject.getString("MSG_STR").trim(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Server Problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }

    public static void bindmListener(BalanceListner listener) {
        balanceListner = listener;
    }
}
