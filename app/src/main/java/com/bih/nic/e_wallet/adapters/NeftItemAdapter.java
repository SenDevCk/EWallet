package com.bih.nic.e_wallet.adapters;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.activities.PayDetailsActivity;
import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.util.List;

/**
 * Created by chandan on 5/13/18.
 */

public class NeftItemAdapter extends RecyclerView.Adapter<NeftItemAdapter.MyViewHolder> {

    private List<NeftEntity> neftEntites;
    Activity activity;
    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, text_wid, text_amount_paid, text_utr, text_time;
        ImageView go_to_statement_view;

        public MyViewHolder(View view) {
            super(view);
            //go_to_statement_view = (ImageView) view.findViewById(R.id.go_to_statement_view);
            text_wid = (TextView) view.findViewById(R.id.text_wid);
            text_amount_paid = (TextView) view.findViewById(R.id.text_amount_paid);
            text_utr = (TextView) view.findViewById(R.id.text_utr);
            text_time = (TextView) view.findViewById(R.id.text_time);
            //description = (TextView) view.findViewById(R.id.text_description);
        }
    }


    public NeftItemAdapter(List<NeftEntity> homeitemEntities, Activity activity) {
        this.neftEntites = homeitemEntities;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.neft_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NeftEntity NeftEntity = neftEntites.get(position);
        holder.text_wid.setText(NeftEntity.getWALLET_ID());
        holder.text_amount_paid.setText("Rs. " + NeftEntity.getAMOUNT());
        holder.text_utr.setText(NeftEntity.getUTR_NO());
        holder.text_time.setText("" + Utiilties.convertTimestampToString(NeftEntity.getTOPUP_TIME()));
        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        //return homeitemEntities.size();
        return neftEntites.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}