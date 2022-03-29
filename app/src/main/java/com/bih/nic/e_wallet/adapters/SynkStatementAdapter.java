package com.bih.nic.e_wallet.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.ConsumerListActivity;
import com.bih.nic.e_wallet.activities.PinCodeActivity;
import com.bih.nic.e_wallet.activities.PrintReceptActivity;
import com.bih.nic.e_wallet.asynkTask.Verifier;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by chandan on 09/05/18.
 */

public class SynkStatementAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    public static ArrayList<Statement> statementMS;
    Activity activity;
    String mdate;
    public static Verifier verifier;
    public SynkStatementAdapter(Activity activity, ArrayList<Statement> statementMS) {
        this.activity=activity;
        layoutInflater = activity.getLayoutInflater();
        this.statementMS = statementMS;

        //Collections.sort(this.statementMS);
        //Collections.reverse(this.statementMS);
    }

    @Override
    public int getCount() {
        return statementMS.size()+1;
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
            convertView = layoutInflater.inflate(R.layout.synk_head, null, false);
        } else {
            ViewHolder viewHolder=new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.statement_item, null, false);
            viewHolder.text_recept_no=(TextView) convertView.findViewById(R.id.text_recept_no);
            viewHolder.text_amount=(TextView) convertView.findViewById(R.id.text_amount);
            viewHolder.text_message_string=(TextView) convertView.findViewById(R.id.text_message_string);
            viewHolder.text_view=(TextView) convertView.findViewById(R.id.text_view);
            viewHolder.name_conid=(TextView) convertView.findViewById(R.id.name_conid);
            viewHolder.text_date=(TextView) convertView.findViewById(R.id.text_date);
            viewHolder.name_conid.setVisibility(View.VISIBLE);
            viewHolder.name_conid.setText("Con ID: "+ statementMS.get(position-1).getCON_ID()+"\n"+ statementMS.get(position-1).getCNAME());

             //android.R.layout.activity_list_item
            if (!statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TC")) {
                //viewHolder.text_message_string.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                if (statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TI")|| statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TP")){
                    viewHolder.text_view.setText("Sync");
                    viewHolder.text_view.setTextColor(activity.getResources().getColor(R.color.colorGreen));
                    if (statementMS.get(position-1).getRCPT_NO().equals("NA")){
                        viewHolder.text_view.setText("Pending");
                        viewHolder.text_view.setTextColor(activity.getResources().getColor(R.color.red));
                    }else{
                        viewHolder.text_view.setText("Sync");
                        viewHolder.text_view.setTextColor(activity.getResources().getColor(R.color.colorGreen));
                    }
                }else if(statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TF")){
                    viewHolder.text_view.setText("failed");
                    viewHolder.text_view.setTextColor(activity.getResources().getColor(R.color.colorPrimaryLight));
                    viewHolder.text_view.setBackgroundColor(activity.getResources().getColor(R.color.colorGrayLight));
                }
                else if(statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TR")){
                    viewHolder.text_view.setText("refunded");
                    viewHolder.text_view.setTextColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
                    viewHolder.text_view.setBackgroundColor(activity.getResources().getColor(R.color.colorGrayLight));
                }
                viewHolder.text_recept_no.setText(statementMS.get(position-1).getTRANS_ID());

            }else{
                viewHolder.text_recept_no.setText(statementMS.get(position-1).getRCPT_NO());
            }
            viewHolder.text_amount.setText("Rs. "+ statementMS.get(position-1).getPAY_AMT());
            viewHolder.text_message_string.setText(statementMS.get(position-1).getMESSAGE_STRING());
            viewHolder.text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=null;
                    if (statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TC")) {
                        intent = new Intent(activity, PrintReceptActivity.class);
                        intent.putExtra("object", statementMS.get(position-1));
                        activity.startActivity(intent);
                    }
                    else if(statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TI")|| statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TP")){
                        UserInfo2 userInfo2= CommonPref.getUserDetails(activity);
                        if (userInfo2!=null) {
                            if (statementMS.get(position-1).getRCPT_NO().equals("NA")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                                alertDialog.setMessage("Payment you have done is pending for this Consumer . If status pending remains after sync then pay again after 15 minutes.");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }else {
                                verifier = (Verifier) new Verifier(SynkStatementAdapter.this,  activity).execute(userInfo2.getUserID() + "|" + GlobalVariables.LoggedUser.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|" + statementMS.get(position - 1).getTRANS_ID());
                            }
                        }
                    }
                    else if(statementMS.get(position-1).getTransStatus().equalsIgnoreCase("TF")){
                        Toast.makeText(activity, "Failed !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            String[] tokens= Utiilties.convertTimestampToStringSlash(statementMS.get(position-1).getPayDate()).split(" ");
            if (position==1){
                mdate=tokens[0].trim();
                viewHolder.text_date.setVisibility(View.VISIBLE);
                viewHolder.text_date.setText(""+ Utiilties.convertTimestampToStringSlash(statementMS.get(position-1).getPayDate()).substring(0,10));
            }else if (!mdate.trim().equalsIgnoreCase(tokens[0].trim())){
                mdate=tokens[0].trim();
                viewHolder.text_date.setVisibility(View.VISIBLE);
                viewHolder.text_date.setText(""+ Utiilties.convertTimestampToStringSlash(statementMS.get(position-1).getPayDate()).substring(0,10));
            }else{
                viewHolder.text_date.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
    class ViewHolder {
        TextView text_recept_no,text_amount,text_message_string,text_view,name_conid,text_date;
    }



}
