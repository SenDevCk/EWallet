package com.bih.nic.e_wallet.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.SynkStatementAdapter;
import com.bih.nic.e_wallet.asynkTask.SyncroniseStatement;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;

import java.util.ArrayList;

public class StatmentSyncronizeActivity extends AppCompatActivity {

    ListView transaction_history_list;
    Toolbar toolbar_synk;
    ArrayList<Statement> statementMS;
    TextView text_warning_statement;
    SynkStatementAdapter synkStatementAdapter;
    SyncroniseStatement syncroniseStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncronize);
        toolbar_synk = (Toolbar) findViewById(R.id.toolbar_synk);
        toolbar_synk.setTitle("Syncronize Payments");
        setSupportActionBar(toolbar_synk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        transaction_history_list = (ListView) findViewById(R.id.transaction_history_list);
        text_warning_statement = (TextView) findViewById(R.id.text_warning_statement);

    }

    @Override
    protected void onResume() {
        super.onResume();
        statementMS = new DataBaseHelper(StatmentSyncronizeActivity.this).getTotalSynkStatements(CommonPref.getUserDetails(StatmentSyncronizeActivity.this).getUserID());
        if (statementMS!=null) {
            if (statementMS.size() > 0) {
                text_warning_statement.setVisibility(View.GONE);
                transaction_history_list.setVisibility(View.VISIBLE);
                synkStatementAdapter = new SynkStatementAdapter(StatmentSyncronizeActivity.this, statementMS);
                transaction_history_list.setAdapter(synkStatementAdapter);
            } else {
                text_warning_statement.setVisibility(View.VISIBLE);
                transaction_history_list.setVisibility(View.GONE);
            }
        }else{
            Toast.makeText(this, "Somthing went wrong !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sync_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark_menu: {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(StatmentSyncronizeActivity.this);
                if (userInfo2 != null) {
                    finish();
                    syncroniseStatement = (SyncroniseStatement) new SyncroniseStatement(StatmentSyncronizeActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo());
                }
                break;
            }
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (syncroniseStatement != null) {
            if (syncroniseStatement.getStatus() == AsyncTask.Status.RUNNING) {
                syncroniseStatement.cancel(true);
            }
        }
        if (SynkStatementAdapter.verifier != null) {
            if (SynkStatementAdapter.verifier.getStatus() == AsyncTask.Status.RUNNING) {
                SynkStatementAdapter.verifier.cancel(true);
            }
        }
        super.onDestroy();
    }
}