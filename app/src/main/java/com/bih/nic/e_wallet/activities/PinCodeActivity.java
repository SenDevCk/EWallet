package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.SynkStatementAdapter;
import com.bih.nic.e_wallet.asynkTask.SyncroniseStatement;
import com.bih.nic.e_wallet.asynkTask.Verifier;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PinCodeActivity extends AppCompatActivity implements View.OnClickListener {

    OtpView otp_view;
    Button verify, go_to_home,but_sync_again;
    MRUEntity mruEntity;
    String amount, mobile;
    String version;
    TextView check_con_pay;
    Animation fade_in;
    private int BLUETOOTH_PERMISSION_CODE = 23;

    int printid1=0;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    JSONObject jsonObject;
    CountDownTimer countDownTimer;
    Verifier verifier;

    @Override
    protected void onStart() {
        GlobalVariables.active=true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        GlobalVariables.active=false;
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        if (getIntent() != null) {
            mruEntity = (MRUEntity) getIntent().getSerializableExtra("object");
            mobile = getIntent().getStringExtra("mobile");
            amount = getIntent().getStringExtra("amount");
        }
        init();
        //new GetOTPAsync().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        otp_view = (OtpView) findViewById(R.id.otp_view);
        verify = (Button) findViewById(R.id.verify);
        go_to_home = (Button) findViewById(R.id.go_to_home);
        but_sync_again = (Button) findViewById(R.id.but_sync_again);
        check_con_pay = (TextView) findViewById(R.id.check_con_pay);
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        check_con_pay.setVisibility(View.GONE);
        but_sync_again.setVisibility(View.GONE);
        verify.setOnClickListener(this);
        go_to_home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_to_home) {
            finish();
        } else {
            if (otp_view.getOTP().length() < 4) {
                Toast.makeText(getApplicationContext(), "Enter Valid OTP", Toast.LENGTH_LONG).show();
            } else {
                verify.setClickable(false);
                AlertDialogForPayment();
            }
        }
    }

    public void AlertDialogForPayment() {
        final Dialog dialog = new Dialog(PinCodeActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/

        dialog.setContentView(R.layout.dialog_verify);
        dialog.setTitle("Verify");
        TextView text_amount = (TextView) dialog.findViewById(R.id.text_amount);
        text_amount.setText("Amount : " + amount.trim());
        Button verify_pay = (Button) dialog.findViewById(R.id.verify_pay);
        Button cancel_pay = (Button) dialog.findViewById(R.id.cancel_pay);
        // if button is clicked, close the custom dialog
        cancel_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        verify_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UserInfo2 userInfo2 = CommonPref.getUserDetails(PinCodeActivity.this);
                Statement statement = new DataBaseHelper(PinCodeActivity.this).getTransStatus(userInfo2, mruEntity.getCON_ID(), Utiilties.getDateString("dd/MM/yyyy"));
                if (statement != null) {
                    if ((statement.getTransStatus().equals("TI") || statement.getTransStatus().equals("TP") || statement.getTransStatus().equals("TC")) && Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()), statement.getPayDate()) <= 15) {
                        check_con_pay.setVisibility(View.VISIBLE);
                        check_con_pay.setText("You already requested for this Consumer payment ! Please either syncronize or verify statement !   You can request for another payment for this Consumer after " + (15 - Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()), statement.getPayDate())) + " minuts / total 15 minuts ! ");
                        check_con_pay.startAnimation(fade_in);
                    } else {
                        check_con_pay.setVisibility(View.GONE);
                        callService(userInfo2);
                    }
                } else {
                    check_con_pay.setVisibility(View.GONE);
                    callService(userInfo2);
                }


            }
        });

        dialog.show();
    }

    private void callService(UserInfo2 userInfo2) {
        new MakePaymentLoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|"
                + userInfo2.getSerialNo() + "|" + mruEntity.getBOOK_NO().trim() + "|" + mruEntity.getCON_ID().trim() + "|" + amount.trim() + "|" + otp_view.getOTP().toString().trim() + "|" + mobile.trim() + "|" + mruEntity.getBILL_NO().trim() + "|M|" + version + "|" + Utiilties.getCurrentDate());

    }

    private class MakePaymentLoader extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog1 = new ProgressDialog(PinCodeActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(PinCodeActivity.this).create();

        @Override
        protected void onPreExecute() {
            check_con_pay.setVisibility(View.GONE);
            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Processing...");
            this.dialog1.show();
            new DataBaseHelper(PinCodeActivity.this).deleteReceptNo_NA_(mruEntity.getCON_ID());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("CON_ID", "" + mruEntity.getCON_ID());
                jsonObject.accumulate("CNAME", "" + mruEntity.getCNAME());
                jsonObject.accumulate("PAY_AMT", "" + amount);
                jsonObject.accumulate("WALLET_BALANCE", "NA");
                jsonObject.accumulate("WALLET_ID", "NA");
                jsonObject.accumulate("RRFContactNo", "NA");
                jsonObject.accumulate("ConsumerContactNo", "" + mobile);
                jsonObject.accumulate("transStatus", "TP");
                jsonObject.accumulate("MESSAGE_STRING", "Failure");
                jsonObject.accumulate("transTime", "" + Utiilties.getCurrentDateWithTime());
                jsonObject.accumulate("BILL_NO", "NA");
                jsonObject.accumulate("payMode", "NA");
                jsonObject.accumulate("TRANS_ID", "NA");
                jsonObject.accumulate("RCPT_NO", "NA");
                new DataBaseHelper(PinCodeActivity.this).saveStatement(jsonObject.toString(), CommonPref.getUserDetails(PinCodeActivity.this).getUserID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            if (Utiilties.isOnline(PinCodeActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    result = WebHandler.callByPostwithoutparameter( Urls_this_pro.MAKE_PAYMENT_URL + reqString(String.valueOf(strings[0])));
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
                result = null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (this.dialog1.isShowing()) this.dialog1.cancel();
            if (result != null) {
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getString("MESSAGE_STRING").equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(PinCodeActivity.this, "" + jsonObject.getString("MESSAGE_STRING"), Toast.LENGTH_SHORT).show();
                        long getsaved = new DataBaseHelper(PinCodeActivity.this).saveStatement(result, CommonPref.getUserDetails(PinCodeActivity.this).getUserID());
                        if (getsaved > 0) {
                            int MyVersion = Build.VERSION.SDK_INT;
                            if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                if (checkAndRequestPermissions()) {
                                    Intent intent = new Intent(PinCodeActivity.this, PrintReceptActivity.class);
                                    intent.putExtra("conid", jsonObject.getString("CON_ID"));
                                    startActivity(intent);
                                }
                            }else {
                                Intent intent = new Intent(PinCodeActivity.this, PrintReceptActivity.class);
                                intent.putExtra("conid", jsonObject.getString("CON_ID"));
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(PinCodeActivity.this, "Data not Saved !", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        //new DataBaseHelper(PinCodeActivity.this).deleteRequestedPayment(mruEntity.getCON_ID(),amount);
                     /*  alertDialog.setMessage(""+jsonObject.getString("MESSAGE_STRING"));
                       alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                           }
                       });
                       alertDialog.show();*/
                     if (jsonObject.getString("transStatus").equals("TP") || jsonObject.getString("transStatus").equals("TI")){
                        if (jsonObject.getString("TRANS_ID").trim()!=null) {
                            if (!jsonObject.getString("TRANS_ID").trim().equals("") && !jsonObject.getString("TRANS_ID").trim().equals("NA")) {
                                countDownTimer = new CountDownTimer(15000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        check_con_pay.setVisibility(View.VISIBLE);
                                        check_con_pay.setText(Html.fromHtml("Please wait for <b style=\"color:Tomato;\">:" + (millisUntilFinished / 1000) + "</b> Seconds till syncronise Pending Payment"));
                                        check_con_pay.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                                        //here you can have your logic to set text to edittext
                                    }

                                    public void onFinish() {
                                        check_con_pay.setVisibility(View.GONE);
                                        UserInfo2 userInfo2 = CommonPref.getUserDetails(PinCodeActivity.this);
                                        if (userInfo2 != null) {
                                            try {
                                                verifier = (Verifier) new Verifier(PinCodeActivity.this).execute(userInfo2.getUserID() + "|" + GlobalVariables.LoggedUser.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + jsonObject.getString("TRANS_ID").trim());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }.start();
                            }
                        }
                     }else {
                         check_con_pay.setVisibility(View.VISIBLE);
                         check_con_pay.setText("" + jsonObject.getString("MESSAGE_STRING"));
                         check_con_pay.startAnimation(fade_in);
                     }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //new DataBaseHelper(PinCodeActivity.this).deleteRequestedPayment(mruEntity.getCON_ID(),amount);
            /*    alertDialog.setMessage("Something went Wrong ! May be Server Problem !");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();*/
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText("Something went Wrong ! May be Server Problem !");
                check_con_pay.startAnimation(fade_in);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            alertDialog.setMessage("Transaction Canceled !");
            alertDialog.setCancelable(false);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
            //new DataBaseHelper(PinCodeActivity.this).deleteRequestedPayment(mruEntity.getCON_ID(), amount);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            alertDialog.setMessage(s);
            alertDialog.setCancelable(false);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
            //new DataBaseHelper(PinCodeActivity.this).deleteRequestedPayment(mruEntity.getCON_ID(), amount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), PinCodeActivity.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }


    private boolean checkAndRequestPermissions() {

        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Intent intent = new Intent(PinCodeActivity.this, PrintReceptActivity.class);
                        intent.putExtra("conid", jsonObject.getString("CON_ID"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Please enable all permissions !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //countDownTimer.cancel();
        super.onDestroy();
    }
}
