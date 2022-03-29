package com.bih.nic.e_wallet.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.entity.GrivanceEntity;


import java.util.ArrayList;

public class GrivanceListAdapter extends BaseAdapter {
    Activity activity;
    String from_date,to_date;
    ArrayList<GrivanceEntity> reportEntities;
    LayoutInflater layoutInflater;
    String save_date;
    public GrivanceListAdapter(Activity activity, ArrayList<GrivanceEntity> reportEntities){
        this.activity=activity;
        //this.from_date=from_date;
        //this.to_date=to_date;
        this.reportEntities=reportEntities;
        layoutInflater=activity.getLayoutInflater();
        //Collections.reverse(this.reportEntities);
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
        convertView=layoutInflater.inflate(R.layout.neft_item, null, false);
        LinearLayout ll_walet=(LinearLayout)convertView.findViewById(R.id.ll_walet);
        LinearLayout ll_tn=(LinearLayout)convertView.findViewById(R.id.ll_tn);
        ll_tn.setVisibility(View.VISIBLE);
        TextView text_wal_id=(TextView)convertView.findViewById(R.id.text_wal_id);
        text_wal_id.setText("Bank Name");
        TextView text_wid=(TextView)convertView.findViewById(R.id.text_wid);
        TextView text_utr=(TextView)convertView.findViewById(R.id.text_utr);
        TextView text_time=(TextView)convertView.findViewById(R.id.text_time);
        TextView text_ticket_no=(TextView)convertView.findViewById(R.id.text_ticket_no);
        TextView text_amount_paid=(TextView)convertView.findViewById(R.id.text_amount_paid);
        final GrivanceEntity grivanceEntity=reportEntities.get(position);
        text_wid.setText(grivanceEntity.getBANK());
        text_utr.setText(grivanceEntity.getUTR_NO());
        text_time.setText(grivanceEntity.getTOPUP_DATE());
        text_ticket_no.setText(""+grivanceEntity.getTICKET_NO());
        text_amount_paid.setText(grivanceEntity.getAMOUNT());
        if (grivanceEntity.getSTATUS().equals("N")){
            ll_walet.setBackgroundColor(activity.getResources().getColor(R.color.holo_red_light));
        }else{
            ll_walet.setBackgroundColor(activity.getResources().getColor(R.color.holo_green_light));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(grivanceEntity);
            }
        });
        return convertView;
    }

    private void setUpDialog(final GrivanceEntity grivanceEntity) {

        final Dialog setup_dialog = new Dialog(activity);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.grivance_details_dialog);
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        ((TextView)setup_dialog.findViewById(R.id.text_tn)).setText(""+grivanceEntity.getTICKET_NO());
        ((TextView)setup_dialog.findViewById(R.id.text_wall_id)).setText(""+grivanceEntity.getWALLET_ID());
        ((TextView)setup_dialog.findViewById(R.id.text_amount)).setText(""+grivanceEntity.getAMOUNT());
        ((TextView)setup_dialog.findViewById(R.id.text_mode)).setText(""+grivanceEntity.getPAY_MODE());
        ((TextView)setup_dialog.findViewById(R.id.text_uid)).setText(""+grivanceEntity.getUSER_ID());
        ((TextView)setup_dialog.findViewById(R.id.text_utr_no)).setText(""+grivanceEntity.getUTR_NO());
        ((TextView)setup_dialog.findViewById(R.id.text_bank)).setText(""+grivanceEntity.getBANK());
        ((TextView)setup_dialog.findViewById(R.id.text_remarks)).setText(""+grivanceEntity.getREMARKS());
        ((TextView)setup_dialog.findViewById(R.id.text_topup_date)).setText(""+grivanceEntity.getTOPUP_DATE());
        ((TextView)setup_dialog.findViewById(R.id.text_date_time)).setText(""+grivanceEntity.getDATE_TIME());
        ((TextView)setup_dialog.findViewById(R.id.text_stas)).setText(""+grivanceEntity.getSTATUS());
        ((TextView)setup_dialog.findViewById(R.id.text_cu_sts)).setText(""+grivanceEntity.getCURRENT_STATUS());
        ((TextView)setup_dialog.findViewById(R.id.text_ms_st)).setText(""+grivanceEntity.getMSG_STR());
        ((ImageView)setup_dialog.findViewById(R.id.close_setup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        setup_dialog.show();
    }
}
