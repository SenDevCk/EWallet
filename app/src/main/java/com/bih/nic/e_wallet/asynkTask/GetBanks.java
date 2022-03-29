package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetBanks extends AsyncTask<String, Void, String> {
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
   Spinner spn_bank;
    Activity activity;
    String req_string="";

    public GetBanks(Activity activity) {
        this.activity = activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
        spn_bank=(Spinner)activity.findViewById(R.id.spn_bank);
    }

    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading...");
        this.dialog1.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        req_string=strings[0];
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    result = WebHandler.callByPostwithoutparameter(Urls_this_pro.GET_BANK+ URLEncoder.encode(String.valueOf(strings[0]+"|"+strings[1]), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return result;

    }

    @Override
    protected void onPostExecute(String res) {
        super.onPostExecute(res);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        try {
            if (res != null) {
                ArrayList<String> banks=new ArrayList<>();
                JSONArray jsonArray=new JSONArray(res);
                for (int i=0;i<jsonArray.length();i++){
                    banks.add(jsonArray.getJSONObject(i).getString("bankName"));
                }
                if (banks.size()>0) {
                    new DataBaseHelper(activity).insertBanks(banks);
                    ArrayList<String> banks2=new DataBaseHelper(activity).getBanks();
                    spn_bank.setAdapter(new ArrayAdapter<String>(activity,R.layout.support_simple_spinner_dropdown_item,banks2));
                    spn_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position>0){
                                GlobalVariables.bank_name=parent.getItemAtPosition(position).toString();
                            }else{
                                GlobalVariables.bank_name="";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            } else {
                Toast.makeText(activity, "Something went Wrong ! ", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();

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
