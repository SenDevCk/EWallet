package com.bih.nic.e_wallet.activities;


import android.os.Bundle;

import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.BookNoAdapter;


/**
 * Created by NIC2 on 1/16/2018.
 */
public class BookNoActivity extends AppCompatActivity {
    ListView book_list;
    Toolbar toolbar_book_no;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_no);
        toolbar_book_no=(Toolbar)findViewById(R.id.toolbar_book_no);
        toolbar_book_no.setTitle("Select Payment");
        setSupportActionBar(toolbar_book_no);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        flag=getIntent().getBooleanExtra("flag",false);
        book_list=(ListView)findViewById(R.id.book_list);
        book_list.setAdapter(new BookNoAdapter(BookNoActivity.this,flag));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
