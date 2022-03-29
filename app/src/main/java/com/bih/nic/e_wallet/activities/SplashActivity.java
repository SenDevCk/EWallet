package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Versioninfo;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.MarshmallowPermission;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    String version;
    TelephonyManager tm;
    private static String imei = "";
    MarshmallowPermission MARSHMALLOW_PERMISSION;
    ProgressBar mprogressBar;
    public static SharedPreferences prefs;
    public static final String SDCARD = String.valueOf(Environment.getExternalStorageDirectory());
    TextView text_ver, text_head_sp;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 10;
    private boolean initse = false;
    private int is_imei_var;

    @Override
    protected void onStart() {
        super.onStart();
        if (CommonPref.getCurrentDate(SplashActivity.this).equals("")) {
            CommonPref.setCurrentDate(SplashActivity.this, Utiilties.getCurrentDateWithTime());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MultiDex.install(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        text_head_sp = (TextView) findViewById(R.id.text_head_sp);
        text_ver = (TextView) findViewById(R.id.text_imei);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/header_font.ttf");
        text_head_sp.setTypeface(face);
        DataBaseHelper db = new DataBaseHelper(this);
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;

        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean net = false;
        TelephonyManager tm = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Call some material design APIs here
            if (checkAndRequestPermissions()) {
                //init2();
                if (!initse) init();
            }

        } else {
            if (!initse) init();
        }
    }

    public void readPhoneState() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(SplashActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    imei = tm.getDeviceId();
                } else {
                    imei = Utiilties.getIMEI_forAndroid10(SplashActivity.this);
                }
                //imei = "861878037876718";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) ;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    imei = tm.getDeviceId();
                } else {
                    imei = Utiilties.getIMEI_forAndroid10(SplashActivity.this);
                }
                //	imei = "861878037876718";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.e("App Version : ", "" + version + " ( " + imei + " )");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showDailog(AlertDialog.Builder ab,
                            final Versioninfo versioninfo) {

        if (!versioninfo.isVerUpdated()) {

            if (versioninfo.getPriority().equals("0")) {
                start();
            } else if (versioninfo.getPriority().endsWith("1")) {

                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());

                ab.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent myWebLink = new Intent(
                                        android.content.Intent.ACTION_VIEW);
                                myWebLink.setData(Uri.parse(versioninfo.getAppUrl()));
                                startActivity(myWebLink);
                                dialog.dismiss();
                            }
                        });
                ab.setNegativeButton("Ignore",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                dialog.dismiss();

                                start();
                            }

                        });

                ab.show();

            } else if (versioninfo.getPriority().equals("2")) {
                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());
                ab.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                                myWebLink.setData(Uri.parse(versioninfo.getAppUrl()));
                                startActivity(myWebLink);
                                dialog.dismiss();

                            }
                        });

                ab.show();
            }
        } else {
            start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    private class CheckUpdate extends AsyncTask<String, Void, Versioninfo> {


        @Override
        protected void onPreExecute() {

        }

        @SuppressLint("WrongThread")
        @Override
        protected Versioninfo doInBackground(String... Params) {
            MARSHMALLOW_PERMISSION = new MarshmallowPermission(SplashActivity.this, android.Manifest.permission.READ_PHONE_STATE);
            if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
                try {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        imei = tm.getDeviceId();
                    } else {
                        imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    }
                    version = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        imei = tm.getDeviceId();
                    } else {
                        imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    }
                    version = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String res = null;
            text_ver.setText("App Version - " + version + " ( " + imei + " )");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userId", "" + CommonPref.getUserDetails(SplashActivity.this).getUserID());
                jsonObject.accumulate("passWord", "" + version);
                jsonObject.accumulate("imeiNo", "" + CommonPref.getUserDetails(SplashActivity.this).getImeiNo());
                jsonObject.accumulate("query", "NSC");
                res = WebHandler.callByPostwithoutparameter(Urls_this_pro.EWALLET_OUIRY_VERSION + URLEncoder.encode(String.valueOf(imei + "|" + version), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Versioninfo versioninfo = WebServiceHelper.CheckVersion(res);
            return versioninfo;
        }

        @Override
        protected void onPostExecute(final Versioninfo versioninfo) {

            final AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
            ab.setCancelable(false);
            if (versioninfo != null) {
                CommonPref.setCheckUpdate(getApplicationContext(), System.currentTimeMillis());
                if (versioninfo.isVerUpdated() == false) {
                    ab.setTitle(versioninfo.getAdminTitle());
                    ab.setMessage(Html.fromHtml(versioninfo.getAdminMsg()));
                    ab.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                    showDailog(ab, versioninfo);

                                }
                            });
                    ab.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            start();
                        }
                    });

                    ab.show();
                } else if (versioninfo.isValidDevice() == false) {
                    showDailog(ab, versioninfo);
                } else {
                    showDailog(ab, versioninfo);
                }
            }

        }
    }

    protected void checkOnline() {
        // TODO Auto-generated method stub
        super.onResume();
        String dateToStr = Utiilties.getCurrentDateWithTime();
        CommonPref.setCurrentDate(SplashActivity.this, dateToStr);
        if (Utiilties.isOnline(SplashActivity.this)) {
            new CheckUpdate().execute();
        } else {

            AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
            ab.setMessage("Internet Connection is not avaliable. Please Turn ON Network Connection OR Continue With Off-line Mode.");
            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(I);
                    //new CheckUpdate().execute();
                }
            });

            ab.setNegativeButton("Continue Offline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                    start();
                }
            });

            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();
        }
    }

    private void checkAppUseMode() {
        if (!Utiilties.isGPSEnabled(SplashActivity.this)) {
            Utiilties.displayPromptForEnablingGPS(SplashActivity.this);
        } else {
            boolean net = false;

            MARSHMALLOW_PERMISSION = new MarshmallowPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
            if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0)
                net = Utiilties.isOnline(SplashActivity.this);
            if (net) {

                if (!prefs.getBoolean("firstTime", false)) {
                    loadAppData();
                } else {
                    new CheckUpdate().execute();
                    //start();
                }
            } else if (!prefs.getBoolean("firstTime", false)) {

                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                        SplashActivity.this).create();
                alertDialog.setTitle("No Internet Connection !");
                alertDialog.setMessage("Enable Internet Connection for the First Time !");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed


                        Intent I = new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                        alertDialog.cancel();


                        //start();
                    }
                });

                alertDialog.show();
            } else {

                if (prefs.getBoolean("firstTime", false))
                    checkOnline();


            }
        }
    }

    private void loadAppData() {
        //new CheckMasterLoader().execute();
        start();
    }

    /* private class CheckMasterLoader extends AsyncTask<Void,Void,ArrayList<GetListForSync>>{
         @Override
         protected ArrayList<GetListForSync> doInBackground(Void... params) {
             String res=new WebHandler().callByGet(Urls_this_pro.GET_lIST_SYNK);
             return WebServiceHelper.getListForSync(res);

         }

         @Override
         protected void onPostExecute(ArrayList<GetListForSync> getListForSyncArrayList) {
             super.onPostExecute(getListForSyncArrayList);
             if (getListForSyncArrayList!=null){
                 long count=new DataBaseHelper(SplashActivity.this).insertSynkData(getListForSyncArrayList);
                 if (count>0){
                     SharedPreferences.Editor editor = prefs.edit();
                     editor.putBoolean("downloadCheckMaster", true);
                     editor.commit();
                     checkPref();
                 }else{
                     Log.e("no data found","in database ChargeMaster");
                 }
             }
         }
     }*/

    private void start() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        SplashActivity.this.finish();
    }

    public void checkPref() {
        boolean p1 = prefs.getBoolean("downloadCheckMaster", false);
        if (p1) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
            start();
        } else {
            Log.e("error", " in DownloadCheckMaster");
        }
    }


    private boolean checkAndRequestPermissions() {
        int read_phone_state = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int read_sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int receve_sms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int write_ex_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (read_phone_state != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (write_ex_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read_sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (receve_sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
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
                    if (!initse) init();
                } else {
                    if (!initse) init();
                }
                break;
        }
    }

    private void init() {
        initse = true;
        String dateToStr = Utiilties.getCurrentDateWithTime();
        if (Utiilties.getCountOfDays(dateToStr, CommonPref.getCurrentDate(SplashActivity.this)) >= 1) {
            readPhoneState();
            checkOnline();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readPhoneState();
                    start();
                }
            }, 1000);
        }
    }


}