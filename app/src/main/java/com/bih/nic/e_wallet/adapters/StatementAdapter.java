package com.bih.nic.e_wallet.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.PrintReceptActivity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by chandan on 09/05/18.
 */

public class StatementAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    ArrayList<Statement> statementMS;
    ArrayList<Statement> newList = new ArrayList<Statement>();
    Activity activity;
    String bookno="";
    String mdate="";

    public StatementAdapter(Activity activity,ArrayList<Statement> statementMS) {
        this.activity=activity;
        layoutInflater = activity.getLayoutInflater();
        this.statementMS = statementMS;
       /* Collections.sort(this.statementMS, new Comparator<Statement>() {
            public int compare(Statement o1, Statement o2) {
                return o1.getPayDate().compareTo(o2.getPayDate());
            }
        });*/
        //Collections.sort(this.statementMS);
        //Collections.reverse(this.statementMS);
    }

    @Override
    public int getCount() {
        return statementMS.size();
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
    public View getView(final int position,View convertView, ViewGroup parent) {
        /*if (position == 0) {
            convertView = layoutInflater.inflate(R.layout.statement_head, null, false);
        } else {*/
            ViewHolder viewHolder=new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.statement_item2, null, false);
            viewHolder.ll_st_head=(LinearLayout) convertView.findViewById(R.id.ll_st_head);
            viewHolder.text_recept_no=(TextView) convertView.findViewById(R.id.text_recept_no);
            viewHolder.text_amount=(TextView) convertView.findViewById(R.id.text_amount);
            viewHolder.text_message_string=(TextView) convertView.findViewById(R.id.text_message_string);
            viewHolder.text_date=(TextView) convertView.findViewById(R.id.text_date);
            viewHolder.text_recept_no.setText("Recept No : "+statementMS.get(position).getRCPT_NO());
            viewHolder.text_amount.setText("Rs. "+ statementMS.get(position).getPAY_AMT());
            if (!statementMS.get(position).getMESSAGE_STRING().equalsIgnoreCase("success"))
                viewHolder.text_message_string.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            viewHolder.text_message_string.setText("Con ID : "+statementMS.get(position).getCON_ID());
            viewHolder.text_view=(TextView) convertView.findViewById(R.id.text_view);
           /* viewHolder.text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity,PrintReceptActivity.class);
                    intent.putExtra("object", statementMS.get(position-1));
                    activity.startActivity(intent);
                }
            });*/
            viewHolder.text_view.setText(""+statementMS.get(position).getCNAME());
            viewHolder.ll_st_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity,PrintReceptActivity.class);
                    intent.putExtra("object", statementMS.get(position));
                    activity.startActivity(intent);
                }
            });
            String[] tokens= Utiilties.convertTimestampToStringSlash(statementMS.get(position).getPayDate()).split(" ");
            /*if (position==0){
                mdate=tokens[0].trim();
                viewHolder.text_date.setVisibility(View.VISIBLE);
                viewHolder.text_date.setText(""+ Utiilties.convertTimestampToStringSlash(statementMS.get(position).getPayDate()).substring(0,10));
            }else*/ if (!mdate.equalsIgnoreCase(tokens[0].trim())){
                mdate=tokens[0].trim();
                viewHolder.text_date.setVisibility(View.VISIBLE);
                viewHolder.text_date.setText(""+ Utiilties.convertTimestampToStringSlash(statementMS.get(position).getPayDate()).substring(0,10));
            }else{
                viewHolder.text_date.setVisibility(View.GONE);
            }
        //}
        return convertView;
    }

    class ViewHolder {
        TextView text_recept_no,text_amount,text_message_string,text_view,text_date;
        LinearLayout ll_st_head;
    }
}
