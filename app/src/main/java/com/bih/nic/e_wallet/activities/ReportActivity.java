package com.bih.nic.e_wallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.ReportAdapter;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.ReportEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {
    ListView list_report;
    TextView text_grand_total, text_report_not_found;
    String from, to;
    Toolbar toolbar_report;
    LinearLayout ll_tag, ll_total;
    double grand_total = 0.00;
    ArrayList<ReportEntity> reportEntities = new ArrayList<>();
    String next_date_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        toolbar_report = (Toolbar) findViewById(R.id.toolbar_report);
        toolbar_report.setTitle("Report");
        setSupportActionBar(toolbar_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ll_tag = (LinearLayout) findViewById(R.id.ll_tag);
        ll_total = (LinearLayout) findViewById(R.id.ll_total);

        text_grand_total = (TextView) findViewById(R.id.text_grand_total);
        text_report_not_found = (TextView) findViewById(R.id.text_report_not_found);
        list_report = (ListView) findViewById(R.id.list_report);

        if (getIntent().hasExtra("from")) {
            from = getIntent().getStringExtra("from");
            to = getIntent().getStringExtra("to");
            for (int i = 0; i <= Utiilties.getCountOfDays(from, to); i++) {
                if (i == 0) {
                    next_date_string = from;
                } else {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = format.parse(next_date_string);
                        Date next_date = Utiilties.incrementDateByOne(date);
                        next_date_string = format.format(next_date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<Statement> statements = new DataBaseHelper(ReportActivity.this).getReport(next_date_string, next_date_string, CommonPref.getUserDetails(ReportActivity.this).getUserID());
                if (statements.size() > 0) {
                    double rep_amount = 0.00;
                    int rep_count = 0;
                    for (Statement statement : statements) {
                        rep_amount = rep_amount + Double.parseDouble(statement.getPAY_AMT());
                        rep_count = rep_count + 1;
                    }
                    ReportEntity reportEntity = new ReportEntity();
                    reportEntity.setDate(next_date_string);
                    reportEntity.setTotal_amount(rep_amount);
                    reportEntity.setNo_of_recept(rep_count);
                    reportEntities.add(reportEntity);
                } else {
                    ReportEntity reportEntity = new ReportEntity();
                    reportEntity.setDate(next_date_string);
                    //reportEntities.add(reportEntity);
                }
            }
        }else{
            Collections.reverse(GlobalVariables.reportEntities);
            reportEntities=GlobalVariables.reportEntities;
        }
        if (reportEntities.size() >0) {
            ll_tag.setVisibility(View.VISIBLE);
            ll_total.setVisibility(View.VISIBLE);
            text_report_not_found.setVisibility(View.GONE);
            list_report.setAdapter(new ReportAdapter(ReportActivity.this, from,to,reportEntities));
        }else{
            ll_tag.setVisibility(View.GONE);
            ll_total.setVisibility(View.GONE);
            text_report_not_found.append("\n Please Syncronise data");
            text_report_not_found.setVisibility(View.VISIBLE);
            list_report.setVisibility(View.GONE);
        }
        for (ReportEntity reportEntity : reportEntities) {
            grand_total = grand_total + reportEntity.getTotal_amount();
            text_grand_total.setText("Rs. " + grand_total);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
