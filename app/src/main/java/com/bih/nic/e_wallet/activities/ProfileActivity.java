package com.bih.nic.e_wallet.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.utilitties.CommonPref;

/**
 * Created by NIC2 on 1/19/2018.
 */
public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar_sel_profile;
    TextView text_deleer_name,text_deleer_acc;
    TextView edit_name2,edit_walet_id,edit_mobile2,edit_subdivision,edit_ifsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar_sel_profile=(Toolbar)findViewById(R.id.toolbar_sel_profile);
        toolbar_sel_profile.setTitle("Profile");
        setSupportActionBar(toolbar_sel_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
    }

    private void init() {
        text_deleer_name=(TextView)findViewById(R.id.text_deleer_name);
        text_deleer_acc=(TextView)findViewById(R.id.text_deleer_acc);
        text_deleer_name.setText(CommonPref.getUserDetails(ProfileActivity.this).getUserName());
        //text_deleer_acc.setText("xxxxxxxx"+walet_id.substring(walet_id.length()-2,walet_id.length()));
        text_deleer_acc.setText(CommonPref.getUserDetails(ProfileActivity.this).getUserID());

        edit_name2=(TextView)findViewById(R.id.edit_name2);
        edit_walet_id=(TextView)findViewById(R.id.edit_walet_id);
        edit_mobile2=(TextView)findViewById(R.id.edit_mobile2);
        edit_subdivision=(TextView)findViewById(R.id.edit_subdivision);
        edit_ifsc=(TextView)findViewById(R.id.edit_ifsc);

        edit_ifsc.setText(CommonPref.getUserDetails(ProfileActivity.this).getIFSCCode());
        edit_name2.setText(CommonPref.getUserDetails(ProfileActivity.this).getUserName());
        //edit_walet_id.setText("xxxxxxxx"+walet_id.substring(walet_id.length()-2,walet_id.length()));
        edit_walet_id.setText(CommonPref.getUserDetails(ProfileActivity.this).getACCT_NO());
        edit_mobile2.setText("xxxxxxx"+CommonPref.getUserDetails(ProfileActivity.this).getContactNo().substring(7,10));
        edit_subdivision.setText(CommonPref.getUserDetails(ProfileActivity.this).getSubdivName());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
