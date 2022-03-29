package com.bih.nic.e_wallet.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.StatementAdapter;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.utilitties.CommonPref;

import java.util.ArrayList;

public class StatementActivity extends AppCompatActivity {
    ListView transaction_history_list;
    Toolbar toolbar_sel_staement;
    ArrayList<Statement> statementMS;
    TextView text_warning_statement;
    EditText edit_rec;
    ToggleButton toggle;
    boolean toggle_val;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        toolbar_sel_staement=(Toolbar)findViewById(R.id.toolbar_sel_staement);
        toolbar_sel_staement.setTitle("Statements");
        setSupportActionBar(toolbar_sel_staement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        transaction_history_list=(ListView)findViewById(R.id.transaction_history_list);
        text_warning_statement=(TextView) findViewById(R.id.text_warning_statement);
        statementMS = new DataBaseHelper(StatementActivity.this).getSuccessStatements(CommonPref.getUserDetails(StatementActivity.this).getUserID(),"",toggle_val);
        if (statementMS.size()>0) {
            text_warning_statement.setVisibility(View.GONE);
            transaction_history_list.setVisibility(View.VISIBLE);
            transaction_history_list.setAdapter(new StatementAdapter(StatementActivity.this, statementMS));
        }else{
            text_warning_statement.setVisibility(View.VISIBLE);
            transaction_history_list.setVisibility(View.GONE);
        }
        toggle=(ToggleButton) findViewById(R.id.toggle_re);
        edit_rec=(EditText) findViewById(R.id.edit_rec);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggle_val=b;
                if (b){
                    edit_rec.setHint("Enter Recept No.");
                }else{
                    edit_rec.setHint("Enter ConID");
                }
            }
        });
        edit_rec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                statementMS = new DataBaseHelper(StatementActivity.this).getSuccessStatements(CommonPref.getUserDetails(StatementActivity.this).getUserID(),charSequence.toString(),toggle_val);
                transaction_history_list.invalidate();
                if (statementMS.size()>0) {
                    text_warning_statement.setVisibility(View.GONE);
                    transaction_history_list.setVisibility(View.VISIBLE);
                    transaction_history_list.setAdapter(new StatementAdapter(StatementActivity.this, statementMS));
                }else{
                    text_warning_statement.setVisibility(View.VISIBLE);
                    transaction_history_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}