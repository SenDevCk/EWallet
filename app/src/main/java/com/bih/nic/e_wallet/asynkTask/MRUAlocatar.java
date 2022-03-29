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

import com.bih.nic.e_wallet.activities.BookNoActivity;
import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.BookNoEntity;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

public class MRUAlocatar extends AsyncTask<String, Void, ArrayList<BookNoEntity>> {
    private final ProgressDialog dialog1;
    private final AlertDialog alertDialog;
    Activity activity;
    boolean flag;
    public MRUAlocatar(Activity activity,boolean flag) {
        this.activity = activity;
        this.flag=flag;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading...");
        this.dialog1.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected ArrayList<BookNoEntity> doInBackground(String... strings) {
        String result = "";
        ArrayList<BookNoEntity> mruEntities = null;
        result = WebHandler.callByPostwithoutparameter(Urls_this_pro.AllOCATED_MRU_URL + reqString(String.valueOf(strings[0])));
        if (result != null) {
            mruEntities = WebServiceHelper.mruBookNo(result);
        }

        return mruEntities;

    }

    @Override
    protected void onPostExecute(ArrayList<BookNoEntity> mruEntities) {
        super.onPostExecute(mruEntities);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (mruEntities != null) {
            if (mruEntities.size() > 0) {
                long c = new DataBaseHelper(activity).saveBookNo(mruEntities, CommonPref.getUserDetails(activity).getUserID());
                if (c > 0) {
                    activity.startActivity(new Intent(activity, BookNoActivity.class).putExtra("flag",flag));
                } else {
                    Toast.makeText(activity, "NO data found !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "NO data found !", Toast.LENGTH_SHORT).show();
            }
        } else {
            alertDialog.setTitle("MRU Downloaded");
            alertDialog.setMessage(" MRU not Downloaded due to some error !");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

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
