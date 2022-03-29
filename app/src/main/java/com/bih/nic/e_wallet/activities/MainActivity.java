package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.asynkTask.BalanceLoader;
import com.bih.nic.e_wallet.asynkTask.GetNEFTService;
import com.bih.nic.e_wallet.asynkTask.MRUAlocatar;
import com.bih.nic.e_wallet.asynkTask.ReportLoader;
import com.bih.nic.e_wallet.asynkTask.SyncroniseStatement;
import com.bih.nic.e_wallet.asynkTask.SyncroniseStatementForMain;
import com.bih.nic.e_wallet.broadcastRecever.PendingTransactionRecever;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.interfaces.BalanceListner;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar_sel_main;
    LinearLayout ll_topup, ll_setup, ll_consumer_list, ll_payment, ll_syncall, ll_statement;
    LinearLayout rel_profile, zxing_barcode_scanner2, change_mpin, share;
    View footer;
    TextView text_bav_bal;
    String[] mruLoaderString;
    BluetoothDevice con_dev = null;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 99;
    int printid1 = 0;
    BalanceLoader balance_loader;
    ImageView header_image, img_rotate;
    Animation rotation;
    ImageView imageViewheader;
    TextView text_header_name, text_header_mobile;
    String date_clik = "";
    TextView text_fromdate, text_todate, text_pen;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    SyncroniseStatement syncroniseStatement;
    SyncroniseStatementForMain syncroniseStatementForMain;
    GetNEFTService getNEFTService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toolbar_sel_main = (Toolbar) findViewById(R.id.toolbar_sel_main);
        toolbar_sel_main.setTitle("");
        setSupportActionBar(toolbar_sel_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar_sel_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigation header
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String code_v = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
            TextView app_name_tip = (TextView) navigationView.findViewById(R.id.app_name_tip);
            app_name_tip.setText("E-Wallet ( " + code_v + "." + version + " ) V");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        View header = navigationView.getHeaderView(0);
        text_header_name = (TextView) header.findViewById(R.id.text_header_name);
        text_header_mobile = (TextView) header.findViewById(R.id.text_header_mobile);
        imageViewheader = (ImageView) header.findViewById(R.id.imageViewheader);

        //imageViewheader.setOnClickListener(this);
        header_image = (ImageView) findViewById(R.id.header_image);
        UserInfo2 userinfo = CommonPref.getUserDetails(MainActivity.this);
        if (userinfo.getUserID().startsWith("2")) {
            header_image.setImageDrawable(getResources().getDrawable(R.drawable.sblogo1));
            imageViewheader.setImageDrawable(getResources().getDrawable(R.drawable.sblogo1));
        } else if (userinfo.getUserID().startsWith("1")) {
            header_image.setImageDrawable(getResources().getDrawable(R.drawable.nblogo));
            imageViewheader.setImageDrawable(getResources().getDrawable(R.drawable.nblogo));

        }
        text_header_name.setText("" + userinfo.getUserName());
        text_header_mobile.setText("" + userinfo.getContactNo());

        text_pen = (TextView) findViewById(R.id.text_pen);
        text_pen.setVisibility(View.GONE);

        ll_topup = (LinearLayout) findViewById(R.id.ll_topup);
        ll_setup = (LinearLayout) findViewById(R.id.ll_setup);
        ll_consumer_list = (LinearLayout) findViewById(R.id.ll_consumer_list);
        ll_payment = (LinearLayout) findViewById(R.id.ll_payment);
        ll_syncall = (LinearLayout) findViewById(R.id.ll_syncall);
        ll_statement = (LinearLayout) findViewById(R.id.ll_statement);
        ll_topup.setOnClickListener(this);
        ll_setup.setOnClickListener(this);
        ll_consumer_list.setOnClickListener(this);
        ll_payment.setOnClickListener(this);
        ll_syncall.setOnClickListener(this);
        ll_statement.setOnClickListener(this);
        rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
        footerinit();
        UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
        if (CommonPref.getCurrentDateForSync(MainActivity.this).equals("")){
            CommonPref.setCurrentDateForSync(MainActivity.this,Utiilties.getCurrentDateWithTime());
        }
        if (Utiilties.isOnline(MainActivity.this) && Utiilties.getCountOfDays(Utiilties.getCurrentDateWithTime(), CommonPref.getCurrentDateForSync(MainActivity.this)) >= 1) {
            syncroniseStatementForMain = (SyncroniseStatementForMain) new SyncroniseStatementForMain(MainActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|1");
        }
        /*if (Utiilties.isOnline(MainActivity.this)) {
            if (userInfo2 != null) {
                img_rotate.startAnimation(rotation);
                BalanceLoader.bindmListener(new BalanceListner() {
                    @Override
                    public void balanceReceived(final double balance) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public synchronized void run() {
                                text_bav_bal.setText(String.valueOf(balance).trim());
                                img_rotate.clearAnimation();
                            }
                        });
                    }
                });
                balance_loader = (BalanceLoader) (new BalanceLoader(MainActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo()));
            }
        }*/
    }

    private void footerinit() {
        //setPendingTransWeakup();
        footer = findViewById(R.id.footer);
        rel_profile = (LinearLayout) footer.findViewById(R.id.rel_profile);
        change_mpin = (LinearLayout) footer.findViewById(R.id.change_mpin);
        share = (LinearLayout) footer.findViewById(R.id.refresh);
        zxing_barcode_scanner2 = (LinearLayout) footer.findViewById(R.id.zxing_barcode_scanner2);
        img_rotate = (ImageView) footer.findViewById(R.id.img_rotate);
        rel_profile.setOnClickListener(this);
        change_mpin.setOnClickListener(this);
        zxing_barcode_scanner2.setOnClickListener(this);
        share.setOnClickListener(this);
        //qrScan = new IntentIntegrator(this);


        text_bav_bal = (TextView) findViewById(R.id.text_bav_bal);
        text_bav_bal.setText(CommonPref.getUserDetails(MainActivity.this).getWalletAmount());


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_tkc) {
            dialogNeft();

        } else if (id == R.id.drawer_topup) {
            dialogReport(1);
        } else if (id == R.id.drawer_server_report) {
            dialogReport(2);
        } else if (id == R.id.drawer_gri_sin) {
            startActivity(new Intent(MainActivity.this, GrievanceEntryActivity.class));
        } else if (id == R.id.drawer_gri_sts) {
            startActivity(new Intent(MainActivity.this, GrivanceListActivity.class));
        } else if (id == R.id.drawer_rep_datewise) {
            startActivity(new Intent(MainActivity.this, NeftDateWiseReportActivity.class));
        } else if (id == R.id.drawer_dow_un) {
            if (Utiilties.isOnline(MainActivity.this)) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                new MRUAlocatar(MainActivity.this, false).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo());
            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.drawer_list_un) {
            startActivity(new Intent(MainActivity.this, UnbilledConsumerListActivity.class));

        }else if(id == R.id.drawer_web_report){
            UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://energybills.bsphcl.co.in/WebApplication1/newjsp.jsp?walletId="+userInfo2.getWalletId().trim())));
            //startActivity(new Intent(MainActivity.this,WebReportActivity.class));
        } else {
            Toast.makeText(this, "Under Process..", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_payment) {
            setUpPaymentDialog();
        } else if (v.getId() == R.id.ll_topup) {
            /* startActivity(new Intent(MainActivity.this, TopupActivity.class));*/
            dialogTopup();
        } else if (v.getId() == R.id.ll_consumer_list) {
            startActivity(new Intent(MainActivity.this, ConsumerListActivity.class).putExtra("from", "bmain"));
        } else if (v.getId() == R.id.ll_setup) {
            setUpDialog();
        } else if (v.getId() == R.id.ll_syncall) {
            //code for synk
            getImageDialog();
        } else if (v.getId() == R.id.ll_statement) {
            Intent intent = new Intent(MainActivity.this, StatementActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.rel_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (v.getId() == R.id.change_mpin) {
            startActivity(new Intent(MainActivity.this, ChangeMpinActivity.class));
        } else if (v.getId() == R.id.zxing_barcode_scanner2) {
            // qrScan.initiateScan();
            CommonPref.logout(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (v.getId() == R.id.refresh) {

            if (Utiilties.isOnline(MainActivity.this)) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                img_rotate.clearAnimation();
                if (userInfo2 != null) {
                    if (balance_loader != null && balance_loader.getStatus() == AsyncTask.Status.RUNNING) {
                        img_rotate.clearAnimation();
                        balance_loader.cancel(true);
                    }
                    img_rotate.startAnimation(rotation);
                    BalanceLoader.bindmListener(new BalanceListner() {
                        @Override
                        public void balanceReceived(final double balance) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public synchronized void run() {
                                    text_bav_bal.setText(String.valueOf(balance).trim());
                                    img_rotate.clearAnimation();
                       }
                            });
                        }
                    });
                    balance_loader = (BalanceLoader) (new BalanceLoader(MainActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo()));
                }
            }
        }
    }

    private void getImageDialog() {
        String[] days_list = {"1 day", "7 days", "30 days", "90 days", "180 days"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Option");
        builder.setItems(days_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                String no_of_day = days_list[which].trim().split(" ")[0];
                UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                if (Utiilties.isOnline(MainActivity.this) && userInfo2 != null) {
                    syncroniseStatement = (SyncroniseStatement) new SyncroniseStatement(MainActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + no_of_day);
                } else {
                    startActivity(new Intent(MainActivity.this, StatmentSyncronizeActivity.class));
                }
            }
        });
        builder.show();
    }

    private void dialogTopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to topup ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!Utiilties.isOnline(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Please go online !", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent browserIntent = null;
                            UserInfo2 userinfo = CommonPref.getUserDetails(MainActivity.this);
                            if (userinfo.getUserID().startsWith("2")) {
                                //webView.loadUrl("https://bihars.b2biz.co.in/BiharDiscom");
                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bihars.b2biz.co.in/BiharDiscom/"));
                                //webView.loadUrl("https://test.b2biz.co.in/BiharDiscom");
                            } else if (userinfo.getUserID().startsWith("1")) {
                                //webView.loadUrl("https://biharn.b2biz.co.in/BiharDiscom");
                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://biharn.b2biz.co.in/NorthBiharDiscom/"));
                            }
                            //browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://test.b2biz.co.in/BiharDiscom"));
                            startActivity(browserIntent);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Topup Dialog");
        alert.show();
    }

    private void dialogNeft() {
        final Dialog setup_dialog = new Dialog(MainActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.neft_dialog);
        // Set dialog title
        setup_dialog.setTitle("");
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        text_fromdate = (TextView) setup_dialog.findViewById(R.id.text_fromdate);
        text_todate = (TextView) setup_dialog.findViewById(R.id.text_todate);
        text_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "F";
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        text_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "T";
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ImageView close_setup = (ImageView) setup_dialog.findViewById(R.id.img_close);
        close_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        Button search_for_mru = (Button) setup_dialog.findViewById(R.id.search_for_mru);
        search_for_mru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                if (text_fromdate.getText().toString().trim().equals("--Select From Date--")) {
                    Toast.makeText(MainActivity.this, "Select From Date", Toast.LENGTH_SHORT).show();
                } else if (text_todate.getText().toString().trim().equals("--Select To Date--")) {
                    Toast.makeText(MainActivity.this, "Select To Date", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int com = formatter.parse(text_todate.getText().toString().trim()).compareTo(formatter.parse(text_fromdate.getText().toString().trim()));
                        if (com <= 0) {
                            Toast.makeText(MainActivity.this, "Selected-To-Date is not correct", Toast.LENGTH_SHORT).show();
                        } else if (Utiilties.monthsBetweenDates(formatter.parse(text_fromdate.getText().toString().trim()), formatter.parse(text_todate.getText().toString().trim())) > 3) {
                            Toast.makeText(MainActivity.this, "Date difference must be 3 months !", Toast.LENGTH_SHORT).show();
                        } else if (!Utiilties.isOnline(MainActivity.this)) {
                            ArrayList<NeftEntity> neftEntities = new DataBaseHelper(MainActivity.this).getTotalNeft(text_fromdate.getText().toString().trim(), text_todate.getText().toString().trim(), CommonPref.getUserDetails(MainActivity.this).getUserID());
                            if (neftEntities.size() > 0) {
                                CommonPref.neftEntities = neftEntities;
                                startActivity(new Intent(MainActivity.this, GetNeftPayActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "NO DATA FOUND !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setup_dialog.dismiss();
                            UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                            getNEFTService = (GetNEFTService) new GetNEFTService(MainActivity.this, text_fromdate.getText().toString().trim(), text_todate.getText().toString().trim(), userInfo2.getUserID()).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + text_fromdate.getText().toString().trim() + "|" + text_todate.getText().toString().trim());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        setup_dialog.show();
    }


    private void dialogReport(final int flag) {
        final Dialog setup_dialog = new Dialog(MainActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.neft_dialog);
        // Set dialog title
        setup_dialog.setTitle("");
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        text_fromdate = (TextView) setup_dialog.findViewById(R.id.text_fromdate);
        text_todate = (TextView) setup_dialog.findViewById(R.id.text_todate);
        text_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "F";
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        text_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "T";
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ImageView close_setup = (ImageView) setup_dialog.findViewById(R.id.img_close);
        close_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        Button search_for_mru = (Button) setup_dialog.findViewById(R.id.search_for_mru);
        search_for_mru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                if (text_fromdate.getText().toString().trim().equals("--Select From Date--")) {
                    Toast.makeText(MainActivity.this, "Select From Date", Toast.LENGTH_SHORT).show();
                } else if (text_todate.getText().toString().trim().equals("--Select To Date--")) {
                    Toast.makeText(MainActivity.this, "Select To Date", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int com = formatter.parse(text_todate.getText().toString().trim()).compareTo(formatter.parse(text_fromdate.getText().toString().trim()));
                        if (com <= 0) {
                            Toast.makeText(MainActivity.this, "Selected-To-Date is not correct", Toast.LENGTH_SHORT).show();
                        } else if (Utiilties.monthsBetweenDates(formatter.parse(text_fromdate.getText().toString().trim()), formatter.parse(text_todate.getText().toString().trim())) > 3) {
                            Toast.makeText(MainActivity.this, "Date difference must be 3 months !", Toast.LENGTH_SHORT).show();
                        } else {

                            setup_dialog.dismiss();
                            if (flag == 1) {
                                Intent intent_report = new Intent(MainActivity.this, ReportActivity.class);
                                intent_report.putExtra("from", text_fromdate.getText().toString().trim());
                                intent_report.putExtra("to", text_todate.getText().toString().trim());
                                startActivity(intent_report);
                            } else {
                                UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                                new ReportLoader(MainActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + text_fromdate.getText().toString().trim() + "|" + text_todate.getText().toString().trim());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        setup_dialog.show();
    }

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (date_clik.equals("F")) {
            text_fromdate.setText(sdf.format(myCalendar.getTime()));
        } else {
            text_todate.setText(sdf.format(myCalendar.getTime()));
        }
        //edittext.setText(sdf.format(myCalendar.getTime()));
    }

    private void setUpPaymentDialog() {
        final Dialog setup_dialog = new Dialog(MainActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.search_layout);
        // Set dialog title
        setup_dialog.setTitle("");
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        final EditText edit_con_id = (EditText) setup_dialog.findViewById(R.id.edit_con_id);
        final EditText edit_acount_no = (EditText) setup_dialog.findViewById(R.id.edit_acount_no);
        final EditText edit_book_no = (EditText) setup_dialog.findViewById(R.id.edit_book_no);

        edit_con_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && edit_acount_no.getText().toString().trim().length() > 0) {
                    edit_acount_no.setText("");
                }
                if (s.length() > 0 && edit_book_no.getText().toString().trim().length() > 0) {
                    edit_book_no.setText("");
                }
            }
        });

        edit_acount_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && edit_con_id.getText().toString().trim().length() > 0) {
                    edit_con_id.setText("");
                }
                if (s.length() > 0 && edit_book_no.getText().toString().trim().length() > 0) {
                    edit_book_no.setText("");
                }
            }
        });
        edit_book_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && edit_acount_no.getText().toString().trim().length() > 0) {
                    edit_acount_no.setText("");
                }
                if (s.length() > 0 && edit_con_id.getText().toString().trim().length() > 0) {
                    edit_con_id.setText("");
                }
            }
        });
        ImageView close_setup = (ImageView) setup_dialog.findViewById(R.id.img_close);
        close_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        Button search_for_mru = (Button) setup_dialog.findViewById(R.id.search_for_mru);
        search_for_mru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                if (edit_con_id.getText().toString().trim().length() > 0) {
                    setup_dialog.dismiss();
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|" + edit_con_id.getText().toString().trim() + "|NA");
                } else if (edit_acount_no.getText().toString().trim().length() > 0) {
                    setup_dialog.dismiss();
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|NA|" + edit_acount_no.getText().toString().trim());
                } else if (edit_book_no.getText().toString().trim().length() > 0) {
                    setup_dialog.dismiss();
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + edit_book_no.getText().toString().trim() + "|NA|NA");
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter Cons Id or Account No or Book No", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setup_dialog.show();

    }

    private void setUpDialog() {
 /* Intent intent=new Intent(MainActivity.this,BookNoActivity.class);
          startActivity(intent);*/
        final Dialog setup_dialog = new Dialog(MainActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.setup_dialog);
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        TextView button_mru = (TextView) setup_dialog.findViewById(R.id.button_mru);
        TextView button_print = (TextView) setup_dialog.findViewById(R.id.button_print);
        ImageView close_setup = (ImageView) setup_dialog.findViewById(R.id.close_setup);
        close_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        button_mru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                setup_dialog.dismiss();
                if (Utiilties.isOnline(MainActivity.this)) {
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(MainActivity.this);
                    new MRUAlocatar(MainActivity.this, true).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo());
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure printer
                setup_dialog.dismiss();
                AlertDialogForPrinter();
            }
        });
        setup_dialog.show();
    }

    public void AlertDialogForPrinter() {
        final Dialog dialog = new Dialog(MainActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/

        dialog.setContentView(R.layout.printerdialog);
        dialog.setTitle("Printers");

        // set the custom dialog components - text, image and button

        final RadioButton epson = (RadioButton) dialog.findViewById(R.id.Epson);
        final RadioButton Analogics = (RadioButton) dialog.findViewById(R.id.Analogics);
        final RadioButton Bixolon = (RadioButton) dialog.findViewById(R.id.bixolon);

        Button Cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        // if button is clicked, close the custom dialog
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button Ok = (Button) dialog.findViewById(R.id.btn_OK);
        // if button is clicked, close the custom dialog
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epson.isChecked() && Analogics.isChecked()) {

                    Toast.makeText(getBaseContext(), "Please Select One Printer", Toast.LENGTH_SHORT).show();
                } else if (epson.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Epson", Toast.LENGTH_SHORT).show();
                    CommonPref.setPrinterType(getApplicationContext(), "E");
                    dialog.dismiss();
                    bluetooth(1);
                } else if (Analogics.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Analogics", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AnalogicsPrinterSetupActivity.class));
                    dialog.dismiss();
                    //bluetooth(2);
                } else if (Bixolon.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Analogics", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    bluetooth(2);
                }

            }
        });

        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

            /*  if (CommonPref.getTooltipShow(this)){
                        text_pen.setVisibility(View.VISIBLE);
                    }*/
        DataBaseHelper db = new DataBaseHelper(MainActivity.this);
        if (db.getPendingCount(CommonPref.getUserDetails(MainActivity.this).getUserID()) > 0) {
            text_pen.setVisibility(View.VISIBLE);
            text_pen.setText("" + db.getPendingCount(CommonPref.getUserDetails(MainActivity.this).getUserID()));
        } else {
            text_pen.setVisibility(View.GONE);
            text_pen.setText("0");
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), MainActivity.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }

    private class MRULoader extends AsyncTask<String, Void, ArrayList<MRUEntity>> {
        private final ProgressDialog dialog1 = new ProgressDialog(MainActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Loading...");
            this.dialog1.show();
        }

        @Override
        protected ArrayList<MRUEntity> doInBackground(String... strings) {
            String result = "";
            mruLoaderString = strings;
            ArrayList<MRUEntity> mruEntities = null;

            if (Utiilties.isOnline(MainActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    result = WebHandler.callByPostwithoutparameter(Urls_this_pro.DOWNLOAD_MRU_URL + reqString(String.valueOf(strings[0])));
                } else {
                    alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                if (result != null) {
                    mruEntities = WebServiceHelper.mruParser(result);
                }
            } else {
                mruEntities = new DataBaseHelper(MainActivity.this).getMRU(CommonPref.getUserDetails(MainActivity.this).getUserID(), strings);
            }


            return mruEntities;

        }

        @Override
        protected void onPostExecute(ArrayList<MRUEntity> mruEntities) {
            super.onPostExecute(mruEntities);
            if (mruEntities != null) {
                if (mruEntities.size() > 0) {
                    long c = new DataBaseHelper(MainActivity.this).saveMru(mruEntities, CommonPref.getUserDetails(MainActivity.this).getUserID());
                    if (c > 0) {
                        Intent intent = new Intent(MainActivity.this, ConsumerListActivity.class);
                        intent.putExtra("from", "main");
                        intent.putExtra("mstring", mruLoaderString);
                        startActivity(intent);
                    }
                } else {
                    alertDialog.setTitle("MRU Download");
                    alertDialog.setMessage(" No data Found !");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            } else {
                alertDialog.setTitle("MRU Download");
                alertDialog.setMessage(" Something went wrong !");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            if (this.dialog1.isShowing()) this.dialog1.cancel();
        }
    }

    private void bluetooth(int printid) {
        // TODO Auto-generated method stub
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(MainActivity.this, "Device does not support Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(MainActivity.this, "Bluetooth about to start.", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            } else {
                if (printid == 1) {
                    printid1 = 1;
                    if (Build.VERSION.SDK_INT < 23) {
                        //Do not need to check the permission
                        startActivity(new Intent(getApplicationContext(), ConfigurePrinterActivity.class));
                    } else {
                        if (checkAndRequestPermissions()) {
                            startActivity(new Intent(getApplicationContext(), ConfigurePrinterActivity.class));
                        }
                    }

                } else if (printid == 2) {
                    printid1 = 2;
                    if (Build.VERSION.SDK_INT < 23) {
                        //Do not need to check the permission
                        startActivity(new Intent(getApplicationContext(), BixolonSetupActivity.class));
                    } else {
                        if (checkAndRequestPermissions()) {
                            startActivity(new Intent(getApplicationContext(), BixolonSetupActivity.class));
                        }
                    }

                }

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            return;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkAndRequestPermissions() {

        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
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
                    if (printid1 == 1) {
                        startActivity(new Intent(getApplicationContext(), ConfigurePrinterActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), BixolonSetupActivity.class));
                    }
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Please enable all permissions !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setPendingTransWeakup() {
        Date when = new Date(System.currentTimeMillis());

        try {
            Intent someIntent = new Intent(MainActivity.this, PendingTransactionRecever.class); // intent to be launched

            // note this could be getActivity if you want to launch an activity
            someIntent.putExtra("time", when.getHours() + ":" + when.getMinutes() + ":" + when.getSeconds());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    MainActivity.this,
                    0, // id, optional
                    someIntent, // intent to launch
                    PendingIntent.FLAG_CANCEL_CURRENT); // PendintIntent flag
            AlarmManager alarms = (AlarmManager) getSystemService(
                    Context.ALARM_SERVICE);
            //
            alarms.setRepeating(AlarmManager.RTC,
                    when.getTime(), AlarmManager.INTERVAL_HALF_HOUR
                    ,
                    pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit ?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                        //finish();
                    }

                }).create().show();

    }


}
