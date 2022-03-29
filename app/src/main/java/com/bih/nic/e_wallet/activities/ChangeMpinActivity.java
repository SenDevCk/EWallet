package com.bih.nic.e_wallet.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChangeMpinActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar_mpin;
    Button button_change_mpin;
    EditText old_m_pin,new_m_pin1,new_m_pin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mpin);
        toolbar_mpin=(Toolbar)findViewById(R.id.toolbar_mpin);
        toolbar_mpin.setTitle("Change M-Pin");
        setSupportActionBar(toolbar_mpin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
    }

    private void init() {
        old_m_pin=(EditText) findViewById(R.id.old_m_pin);
        new_m_pin1=(EditText) findViewById(R.id.new_m_pin1);
        new_m_pin2=(EditText) findViewById(R.id.new_m_pin2);
        button_change_mpin=(Button)findViewById(R.id.button_change_mpin);
        button_change_mpin.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (old_m_pin.getText().toString().trim().length()<4) {
            old_m_pin.setError("Enter Valid OLD M-PIN");
        }
        else if (new_m_pin1.getText().toString().trim().length()<4) {
            new_m_pin1.setError("Enter Valid New M-PIN");
        }
        else if (new_m_pin2.getText().toString().trim().length()<4 || !new_m_pin2.getText().toString().trim().equals(new_m_pin1.getText().toString().trim())) {
            new_m_pin1.setError("Enter Valid Conform new M-PIN");
        }
        else {
            if (Utiilties.isOnline(ChangeMpinActivity.this)) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(ChangeMpinActivity.this);
                if (userInfo2 != null) {
                    new MpinChanger().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo()+"|"+old_m_pin.getText().toString().trim()+"|"+new_m_pin2.getText().toString().trim());
                }
            }
            else
                Toast.makeText(this, "Go Online !", Toast.LENGTH_SHORT).show();
        }
    }

    class MpinChanger extends AsyncTask<String,Void,String>{
        final ProgressDialog progressDialog=new ProgressDialog(ChangeMpinActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Changing Pin...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String res="";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                res= WebHandler.callByPostwithoutparameter( Urls_this_pro.CHANGE_MPIN + reqString(String.valueOf(strings[0])));
            }else{
                Toast.makeText(ChangeMpinActivity.this, "Your device must be KITKAT or Above !", Toast.LENGTH_SHORT).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (s!=null){
                try {
                    if (s.equalsIgnoreCase("\"SUCCESS\"")){
                        Toast.makeText(ChangeMpinActivity.this, "MPIN Changed Successfully !", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ChangeMpinActivity.this, "MPIN Not Changed !", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ChangeMpinActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ChangeMpinActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),ChangeMpinActivity.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = Base64.encodeToString(chipperdata, Base64.NO_WRAP );//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
}
