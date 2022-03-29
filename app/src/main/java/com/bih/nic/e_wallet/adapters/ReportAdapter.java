package com.bih.nic.e_wallet.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.entity.ReportEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.util.ArrayList;
import java.util.Collections;

public class ReportAdapter extends BaseAdapter {
    Activity activity;
    String from_date,to_date;
    ArrayList<ReportEntity> reportEntities;
    LayoutInflater layoutInflater;
    String save_date;
    public ReportAdapter(Activity activity, String from_date,String to_date, ArrayList<ReportEntity> reportEntities){
        this.activity=activity;
        this.from_date=from_date;
        this.to_date=to_date;
        this.reportEntities=reportEntities;
        layoutInflater=activity.getLayoutInflater();
        Collections.reverse(this.reportEntities);
    }
    @Override
    public int getCount() {
        return (int) reportEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return reportEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=layoutInflater.inflate(R.layout.report_item, null, false);
        TextView text_date=(TextView)convertView.findViewById(R.id.text_date);
        TextView report_recept_no=(TextView)convertView.findViewById(R.id.report_recept_no);
        TextView report_amount=(TextView)convertView.findViewById(R.id.report_amount);
        LinearLayout ll_st_head=(LinearLayout)convertView.findViewById(R.id.ll_st_head);
        text_date.setText(""+ reportEntities.get(position).getDate());
        report_recept_no.setText(""+ reportEntities.get(position).getNo_of_recept());
        report_amount.setText("Rs. "+ reportEntities.get(position).getTotal_amount());
        if (reportEntities.get(position).getVerifyStatus()!=null){
            if (!reportEntities.get(position).getVerifyStatus().equals("VERIFIED")){
                ll_st_head.setBackgroundColor(activity.getResources().getColor(R.color.holo_red_light));
            }else{
                ll_st_head.setBackgroundColor(activity.getResources().getColor(R.color.holo_green_light));
            }
        }
        return convertView;
    }
}
