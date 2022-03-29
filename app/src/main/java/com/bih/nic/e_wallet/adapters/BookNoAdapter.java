package com.bih.nic.e_wallet.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.ConsumerListActivity;
import com.bih.nic.e_wallet.activities.LoginActivity;
import com.bih.nic.e_wallet.asynkTask.DownloadUnbuiledConsumerService;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.BookNoEntity;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by chandan on 4/14/2018.
 */

public class BookNoAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<BookNoEntity> bookNoEntities;
    Activity activity;
    String bookno="";
    boolean flag;
    public BookNoAdapter(Activity activity,boolean flag) {
        this.activity=activity;
        this.flag=flag;
        layoutInflater = activity.getLayoutInflater();
        bookNoEntities = new DataBaseHelper(activity).getBookNo(CommonPref.getUserDetails(activity).getUserID());
    }

    @Override
    public int getCount() {
        return bookNoEntities.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = layoutInflater.inflate(R.layout.book_head, null, false);
        } else {
            ViewHolder viewHolder=new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.book_no_item, null, false);
            viewHolder.text_book_no=(TextView) convertView.findViewById(R.id.text_book_no);
            viewHolder.text_message_string=(TextView) convertView.findViewById(R.id.text_message_string);

            viewHolder.text_book_no.setText(bookNoEntities.get(position-1).getBookNo());
            viewHolder.text_message_string.setText(bookNoEntities.get(position-1).getMessageString());
            if (!bookNoEntities.get(position-1).getMessageString().equals("success"))
                viewHolder.text_message_string.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            viewHolder.text_view=(TextView) convertView.findViewById(R.id.text_view);
            viewHolder.text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookno=bookNoEntities.get(position-1).getBookNo();
                    UserInfo2 userInfo2= CommonPref.getUserDetails(activity);
                    if (flag) {
                        new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + bookno + "|NA|NA");
                    }else{
                        new DownloadUnbuiledConsumerService(activity).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + bookno);
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView text_book_no,text_message_string,text_view;
    }
    private class MRULoader extends AsyncTask<String,Void,ArrayList<MRUEntity>> {
        private final ProgressDialog dialog1 = new ProgressDialog(activity);
        private final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        @Override
        protected void onPreExecute() {
            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Loading MRU...");
            this.dialog1.show();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<MRUEntity> doInBackground(String... strings) {
            String result="";
            ArrayList<MRUEntity> mruEntities=null;
                if (Utiilties.isOnline(activity)) {
                    result = WebHandler.callByPostwithoutparameter(Urls_this_pro.DOWNLOAD_MRU_URL + reqString(String.valueOf(strings[0])));
                    if (result!=null) {
                        mruEntities= WebServiceHelper.mruParser(result);
                    }
                }else{
                    mruEntities=new DataBaseHelper(activity).getMRU(bookno,CommonPref.getUserDetails(activity).getUserID());
                }



            return mruEntities;

        }

        @Override
        protected void onPostExecute(ArrayList<MRUEntity> mruEntities) {
            super.onPostExecute(mruEntities);

            if (bookNoEntities!=null) {
                long c=new DataBaseHelper(activity).saveMru(mruEntities,CommonPref.getUserDetails(activity).getUserID());
                if (this.dialog1.isShowing()) this.dialog1.cancel();
                if (c>0) {
                    Intent intent=new Intent(activity,ConsumerListActivity.class);
                    intent.putExtra("bookno",""+bookno);
                    intent.putExtra("from","adapter");
                    activity.startActivity(intent);
                }else {
                    Log.e("eroor on BookNoAdapter","saveMru(mruEntities) Eroor!");
                }
            }else{
                if (this.dialog1.isShowing()) this.dialog1.cancel();
                alertDialog.setTitle("MRU Downloaded");
                alertDialog.setMessage(" MRU not Downloaded !");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
}
