package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

public class SmsVerificationService extends AsyncTask<String, Void, String> {

    private Activity activity;
    private  ProgressDialog dialog1;
    private  AlertDialog alertDialog;
    String imei,serial_id;
    EditText edit_user_name,edit_pass;


    public SmsVerificationService(Activity activity, String imei, String serial_id) {
        this.activity = activity;
        this.imei=imei;
        this.serial_id=serial_id;
        dialog1 = new ProgressDialog( this.activity);
        alertDialog = new AlertDialog.Builder( this.activity).create();
        edit_user_name=(EditText)this.activity.findViewById(R.id.edit_user_name);
        edit_pass=(EditText)this.activity.findViewById(R.id.edit_pass);
    }

    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Verifying...");
        this.dialog1.show();
    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d("params",strings[0]);
                result = WebHandler.callByPostwithoutparameter(Urls_this_pro.GET_VALIDATE_OTP + reqString(String.valueOf(strings[0])));
            } else {
               Log.e("error","Your device must have atleast Kitkat or Above Version");
            }
        } else {
            Log.e("error","No Internet Connection !");
        }
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (result != null) {
            Log.e("res","res varification :"+result);
            if (result.contains("SUCCESS")) {
               new LoginLoader(activity,imei,serial_id).execute(edit_user_name.getText().toString() + "|" + edit_pass.getText().toString() + "|" + imei + "|" + serial_id);
            } else {
                Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Server Problem", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }


}