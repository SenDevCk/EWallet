package com.bih.nic.e_wallet.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.asynkTask.NeftDatewiseService;
import com.bih.nic.e_wallet.utilitties.Utiilties;

public class NeftDateWiseReportActivity extends AppCompatActivity {

    Toolbar toolbar_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neft_date_wise_report);
        toolbar_report = (Toolbar) findViewById(R.id.toolbar_report_neft);
        toolbar_report.setTitle("NEFT report (Datewise)");
        setSupportActionBar(toolbar_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Utiilties.isOnline(NeftDateWiseReportActivity.this)){
            new NeftDatewiseService(NeftDateWiseReportActivity.this).execute();
        }else{
            Toast.makeText(this, "Please Enable internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
