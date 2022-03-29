package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.LoginActivity;
import com.bih.nic.e_wallet.activities.MainActivity;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;
import com.mukesh.OtpView;

public class LoginLoader extends AsyncTask<String, Void, UserInfo2> {
    
    Activity activity;
    EditText edit_user_name,edit_pass;
    String imei,serial_id;
    OtpView otp_view;
    private final AlertDialog alertDialog;
    private ProgressDialog dialog1;
    public LoginLoader( Activity activity,String imei,String serial_id){
        this.activity=activity;
        this.imei=imei;
        this.serial_id=serial_id;
        edit_user_name=(EditText)this.activity.findViewById(R.id.edit_user_name);
        edit_pass=(EditText)this.activity.findViewById(R.id.edit_pass);
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }


    @Override
    protected void onPreExecute() {
        dialog1.setCancelable(false);
        dialog1.setMessage("Logging...");
        dialog1.show();
    }

    @Override
    protected UserInfo2 doInBackground(String... strings) {
        UserInfo2 userInfo2 = null;
        String res = "";
        if (Utiilties.isOnline(activity)) {
            String result = null;
            //result = WebHandler.callByPostwithoutparameter(strings[0], Urls_this_pro.LOG_IN_URL + URLEncoder.encode(String.valueOf(strings[0]), "UTF-8"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter( Urls_this_pro.LOG_IN_URL+reqString(String.valueOf(strings[0])));
            }else{
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            if (result != null) {
                userInfo2 = WebServiceHelper.loginParser(result, serial_id);
            }
            return userInfo2;
        } else {
            userInfo2 = CommonPref.getUserDetails(activity);
            if (userInfo2.getUserID().length() > 4) {
                userInfo2.setAuthenticated(true);
                return userInfo2;
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(UserInfo2 result) {
        super.onPostExecute(result);
        if (dialog1.isShowing()) { dialog1.cancel();}
            if (result == null ) {
                alertDialog.setTitle("Login Unsuccessful");
                alertDialog.setMessage("Server not responding");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //edit_user_name.setFocusable(true);
                    }
                });
                alertDialog.show();
            } else if (result != null && !result.getAuthenticated()){
                CommonPref.setUserDetails(activity, result);
                if (result.getMessageString().contains("ENTRY")){
                    AlertDialogForCheckDetails(true);
                }
                else if (result.getMessageString().contains("AUTO")){
                    //AlertDialogForCheckDetails(false);
                    dialog1.setCancelable(false);
                    dialog1.setMessage("Waiting for SMS...");
                    dialog1.show();

                }else{
                    alertDialog.setTitle("Login Unsuccessful");
                    alertDialog.setMessage(""+result.getMessageString());
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            edit_user_name.setFocusable(true);
                        }
                    });
                    alertDialog.show();
                }
            }
            else {
                Intent cPannel = new Intent(activity, MainActivity.class);
                if (Utiilties.isOnline(activity)) {
                    if (result != null) {
                        if (imei.equalsIgnoreCase(result.getImeiNo())) {
                            try {
                                result.setPassword(edit_pass.getText().toString());
                                GlobalVariables.LoggedUser = result;
                                CommonPref.setUserDetails(activity, result);
                                activity.startActivity(cPannel);
                                activity.finish();
                            } catch (Exception ex) {
                                Toast.makeText(activity, "Login failed due to Some Error !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alertDialog.setTitle("Device Not Registered");
                            alertDialog.setMessage(""+result.getMessageString());
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    edit_user_name.setFocusable(true);
                                }
                            });
                            alertDialog.show();
                        }
                    }
                } else {
                    if (CommonPref.getUserDetails(activity) != null) {
                        GlobalVariables.LoggedUser = result;
                        if (GlobalVariables.LoggedUser.getUserID().equalsIgnoreCase(edit_user_name.getText().toString().trim()) && GlobalVariables.LoggedUser.getPassword().equalsIgnoreCase(edit_pass.getText().toString().trim())) {
                            activity.startActivity(cPannel);
                            activity.finish();
                        } else {
                            Toast.makeText(activity, "User name and password not matched !", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(activity, "Please enable internet connection for first time login.", Toast.LENGTH_LONG).show();
                    }
                }
            }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = Base64.encodeToString(chipperdata, Base64.NO_WRAP);
        Log.e("string",""+encString);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        Log.e("string",""+encString);
        return encString;
    }
    public void AlertDialogForCheckDetails(boolean flag) {
        final Dialog dialog = new Dialog(activity);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/

        dialog.setContentView(R.layout.otp_dialog);
        dialog.setTitle("Enter PIN");
        otp_view = (OtpView) dialog.findViewById(R.id.otp_view);
        otp_view.setEnabled(flag);
        Button verify_pay = (Button) dialog.findViewById(R.id.verify);
        Button go_to_home = (Button) dialog.findViewById(R.id.go_to_home);
        // if button is clicked, close the custom dialog
        go_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        verify_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UserInfo2 userInfo2=CommonPref.getUserDetails(activity);
                new SmsVerificationService(activity,imei,serial_id).execute(edit_user_name.getText().toString().trim()+"|"+imei.trim()+"|"+otp_view.getOTP());
            }
        });
        dialog.show();
    }
}
