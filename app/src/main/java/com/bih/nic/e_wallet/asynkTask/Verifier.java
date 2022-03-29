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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.activities.PrintReceptActivity;
import com.bih.nic.e_wallet.adapters.SynkStatementAdapter;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class Verifier extends AsyncTask<String, Void, String> {
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
    SynkStatementAdapter synkStatementAdapter;
    Activity activity;
    String req_string="";
    public Verifier(SynkStatementAdapter synkStatementAdapter, Activity activity) {

        this.synkStatementAdapter = synkStatementAdapter;
        this.activity = activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    public Verifier(Activity activity) {
        this.activity = activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
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
        req_string=strings[0];
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter(Urls_this_pro.MAKE_verify_URL + reqString(String.valueOf(strings[0])));
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
                final JSONObject jsonObject = new JSONObject(res);
                if (jsonObject.getString("transStatus").equals("TC")) {
                    long up = new DataBaseHelper(activity).updateStatment(jsonObject);
                    if (synkStatementAdapter != null) {
                        if (up > 0) {
                            Log.d("updated", "yes");
                            SynkStatementAdapter.statementMS = new DataBaseHelper(activity).getTotalSynkStatements(CommonPref.getUserDetails(activity).getUserID());
                            synkStatementAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("updated", "No........");
                        }
                    } else {
                        Intent intent = new Intent(activity, PrintReceptActivity.class);
                        intent.putExtra("conid", jsonObject.getString("CON_ID"));
                        activity.startActivity(intent);
                    }
                } else {
                    //Toast.makeText(activity, "Verify not Successful ! ", Toast.LENGTH_SHORT).show();
                    long up = new DataBaseHelper(activity).updateStatment(jsonObject);
                    if (synkStatementAdapter != null) {
                        if (up > 0) {
                            Log.d("updated", "yes");
                            SynkStatementAdapter.statementMS = new DataBaseHelper(activity).getTotalSynkStatements(CommonPref.getUserDetails(activity).getUserID());
                            synkStatementAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("updated", "No.........");
                        }
                    } else {
                        //code for button sync again
                        if (GlobalVariables.active) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        syncStatement();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }

                }
            } else {
                Toast.makeText(activity, "Something went Wrong ! ", Toast.LENGTH_SHORT).show();
                syncStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();
                syncStatement();
        }
    }

    private void syncStatement() {
        TextView check_con_pay = (TextView) activity.findViewById(R.id.check_con_pay);
        check_con_pay.setTextColor(activity.getResources().getColor(android.R.color.holo_orange_dark));
        check_con_pay.setText("Transaction Pending ... Click Sync Again !");
        Button but_sync_again = (Button) activity.findViewById(R.id.but_sync_again);
        but_sync_again.setVisibility(View.VISIBLE);
        but_sync_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(activity);
                if (Utiilties.isOnline(activity)) {
                    new Verifier( activity).execute(req_string);
                } else {
                    Toast.makeText(activity, "Go online !", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
