package com.bih.nic.e_wallet.activities;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.asynkTask.GetBanks;
import com.bih.nic.e_wallet.asynkTask.GrievanceLoader;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

public class GrievanceEntryActivity extends AppCompatActivity implements View.OnClickListener {
    private int mYear, mMonth, mDay;
    DatePickerDialog datedialog;
    TextView tvdob;
    Spinner spn_paymode, spn_bank;
    TextInputEditText etben_fmlyhis, etben_amt,etben_sms;
    Button button_submit;
    ImageView btncaladob;
    CheckBox check_term;
    String[] rech_type = {"--SELECT TYPE--", "NEFT", "RTGS", "IMPS", "PAYMENT GATEWAY","UPI"};
    String version;

    private static String pay_type = "";
    Toolbar toolbar_sel_staement;
    UserInfo2 userInfo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_entry);
        userInfo2=CommonPref.getUserDetails(GrievanceEntryActivity.this);
        toolbar_sel_staement = (Toolbar) findViewById(R.id.toolbar_single_rec);
        toolbar_sel_staement.setTitle("Recharge Grievance");
        setSupportActionBar(toolbar_sel_staement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        UserInfo2 userInfo2 = CommonPref.getUserDetails(GrievanceEntryActivity.this);
        init();
        try {
            version = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> banks = new DataBaseHelper(GrievanceEntryActivity.this).getBanks();
        if (banks.size() <= 1) {
            new GetBanks(GrievanceEntryActivity.this).execute(userInfo2.getImeiNo(), version);
        } else {
            spn_bank.setAdapter(new ArrayAdapter<String>(GrievanceEntryActivity.this, R.layout.support_simple_spinner_dropdown_item, banks));
            spn_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        GlobalVariables.bank_name = parent.getItemAtPosition(position).toString();
                    } else {
                        GlobalVariables.bank_name = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void init() {
        tvdob = (TextView) findViewById(R.id.tvdob);
        spn_bank = (Spinner) findViewById(R.id.spn_bank);
        spn_paymode = (Spinner) findViewById(R.id.spn_paymode);
        etben_fmlyhis = (TextInputEditText) findViewById(R.id.etben_fmlyhis);
        etben_amt = (TextInputEditText) findViewById(R.id.etben_amt);
        etben_sms = (TextInputEditText) findViewById(R.id.etben_sms);
        button_submit = (Button) findViewById(R.id.button_submit);
        btncaladob = (ImageView) findViewById(R.id.btncaladob);
        check_term = (CheckBox) findViewById(R.id.check_term);
        btncaladob.setOnClickListener(this);
        button_submit.setOnClickListener(this);

        spn_paymode.setAdapter(new ArrayAdapter<String>(GrievanceEntryActivity.this, R.layout.support_simple_spinner_dropdown_item, rech_type));
        spn_paymode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    pay_type = parent.getItemAtPosition(position).toString().trim();
                } else {
                    pay_type = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_submit) {
            if (validate()) {
                try {
                    new GrievanceLoader(GrievanceEntryActivity.this).execute(userInfo2.getUserID().trim()+"|"+userInfo2.getPassword().trim()+"|"+userInfo2.getImeiNo().trim()+"|"+userInfo2.getSerialNo().trim()+"|"+etben_fmlyhis.getText().toString().trim()+"|"+pay_type.trim()+"|"+etben_amt.getText().toString().trim()+"|"+tvdob.getText().toString().trim()+"|"+userInfo2.getACCT_NO().trim(),GlobalVariables.bank_name.trim(), Base64.encodeToString(etben_sms.getText().toString().trim().getBytes("UTF-8"), Base64.DEFAULT).replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS").replaceAll("\n",""));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        } else if (v.getId() == R.id.btncaladob) {
            ShowDialog();
        }
    }

    private boolean validate() {
        boolean var_validate = false;
        if (pay_type.trim().equals("")) {
            Toast.makeText(this, "Select Recharge Type !", Toast.LENGTH_SHORT).show();
            var_validate = false;
        } else if (GlobalVariables.bank_name.trim().equals("")) {
            Toast.makeText(this, "Select Bank !", Toast.LENGTH_SHORT).show();
            var_validate = false;
        } else if (etben_fmlyhis.getText().toString().trim().length() <= 0) {
            var_validate = false;
            etben_fmlyhis.setError("Enter UTR No./REF");
        } else if (etben_amt.getText().toString().trim().length() <= 0) {
            var_validate = false;
            etben_amt.setError("Enter Amount");
        }
        else if (etben_sms.getText().toString().trim().length() <= 0) {
            var_validate = false;
            etben_sms.setError("Enter SMS");
        }
        else if (etben_sms.getText().toString().trim().length() > 255) {
            var_validate = false;
            etben_sms.setError("Max Length 255 !");
        }
        else if (tvdob.getText().toString().trim().length() <= 0) {
            var_validate = false;
            Toast.makeText(this, "Select Date !", Toast.LENGTH_SHORT).show();
        } else if (!check_term.isChecked()) {
            Toast.makeText(this, "Please Check terms !", Toast.LENGTH_SHORT).show();
        } else {
            var_validate = true;
        }

        return var_validate;
    }

    private void ShowDialog() {


        Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        datedialog = new DatePickerDialog(GrievanceEntryActivity.this,
                mDateSetListener, mYear, mMonth, mDay);
        datedialog.getDatePicker().setMaxDate(System.currentTimeMillis()-2);
        datedialog.show();

    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            mYear = selectedYear;
            mMonth = selectedMonth;
            mDay = selectedDay;
            try {
                if (mDay < 10 &&( mMonth+1) > 9) {
                    mDay = Integer.parseInt("0" + mDay);
                    tvdob.setText(new StringBuilder().append("0" + mDay).append("/").append(mMonth + 1).append("/").append(mYear));
                }else if ((mMonth+1)<10 && mDay > 9){
                    mMonth = Integer.parseInt("0" + mMonth);
                    tvdob.setText(new StringBuilder().append( mDay).append("/").append("0" +(mMonth + 1)).append("/").append(mYear));
                }
                else if ((mMonth+1)<10 && mDay <10){
                    mDay = Integer.parseInt("0" + mDay);
                    mMonth = Integer.parseInt("0" + mMonth);
                    tvdob.setText(new StringBuilder().append("0" + mDay).append("/").append("0" +(mMonth + 1)).append("/").append(mYear));
                }
                else {
                    tvdob.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        GlobalVariables.bank_name = "";
        pay_type = "";
        super.onDestroy();
    }
}
