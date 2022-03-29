package com.bih.nic.e_wallet.activities;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.ConsumerItemAdapter;
import com.bih.nic.e_wallet.adapters.NeftItemAdapter;
import com.bih.nic.e_wallet.utilitties.CommonPref;

public class GetNeftPayActivity extends AppCompatActivity {
Toolbar toolbar_getneft;
RecyclerView recycler_list_neft;
NeftItemAdapter neftItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_neft_pay);
        toolbar_getneft=(Toolbar)findViewById(R.id.toolbar_getneft);
        toolbar_getneft.setTitle("NEFT PAY");
        setSupportActionBar(toolbar_getneft);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recycler_list_neft=(RecyclerView)findViewById(R.id.recycler_list_neft);
        if (CommonPref.neftEntities.size() > 0) {
            //text_no_data_found.setVisibility(View.GONE);
            neftItemAdapter = new NeftItemAdapter(CommonPref.neftEntities, GetNeftPayActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GetNeftPayActivity.this);
            recycler_list_neft.setLayoutManager(mLayoutManager);
            recycler_list_neft.setItemAnimator(new DefaultItemAnimator());
            recycler_list_neft.setAdapter(neftItemAdapter);
        } else {
            //text_no_data_found.setVisibility(View.VISIBLE);
            recycler_list_neft.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
