package com.bih.nic.e_wallet.asynkTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.UnbilledConsumerListActivity;
import com.bih.nic.e_wallet.adapters.UnbilledConsumerAdapter;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;

public class DownloadUnbuiledConsumerService extends AsyncTask<String,Void,ArrayList<MRUEntity>> {
    Activity activity;
    private ProgressDialog dialog1;
    String uid;
    RecyclerView recycler_list_consumer;
    TextView text_no_data_found;
    UnbilledConsumerAdapter consumerItemAdapter;
    private AlertDialog alertDialog;
    public DownloadUnbuiledConsumerService(Activity activity){
        this.activity=activity;
        dialog1=new ProgressDialog(activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
        text_no_data_found=(TextView) this.activity.findViewById(R.id.text_no_data_found);
        recycler_list_consumer=(RecyclerView)this.activity.findViewById(R.id.recycler_list_consumer);
    }
    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<MRUEntity> doInBackground(String... strings) {
        String result="";
        uid=strings[0].split("\\|")[0];
        ArrayList<MRUEntity> mruEntities=null;
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter(Urls_this_pro.GET_UNBUILED_CONSUMER + reqString(String.valueOf(strings[0])));
            }else{
                Toast.makeText(activity, "Your device must be KITKAT or Above !", Toast.LENGTH_SHORT).show();
            }
            if (result!=null) {
                mruEntities = WebServiceHelper.unbilledConsumerParser(result);
            }
        }else{
            mruEntities=new DataBaseHelper(activity).getUnbuiledConsumer(CommonPref.getUserDetails(activity).getUserID(),strings);
        }
        return mruEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<MRUEntity> mruEntities) {
        super.onPostExecute(mruEntities);
        //if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (mruEntities!=null) {
            if (mruEntities.size()>0){
                long c= new DataBaseHelper(activity).saveUnbuiledConsumer(mruEntities,uid);
                if (c>0){
                    Toast.makeText(activity, "All Consumer saved ! Go to Unbilled Consumer List", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "Consumer not saved !", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, "Consumer not found !", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }
        if (dialog1.isShowing()) dialog1.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = Base64.encodeToString(chipperdata, Base64.NO_WRAP);
        Log.e("string",""+encString);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        Log.e("string",""+encString);
        return encString;
    }
}