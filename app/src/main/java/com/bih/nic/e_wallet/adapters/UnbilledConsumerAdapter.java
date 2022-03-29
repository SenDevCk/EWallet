package com.bih.nic.e_wallet.adapters;

import android.app.Activity;
import android.content.Intent;
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
import com.bih.nic.e_wallet.entity.MRUEntity;

import java.util.List;

/**
 * Created by chandan on 5/13/18.
 */

public class UnbilledConsumerAdapter extends RecyclerView.Adapter<UnbilledConsumerAdapter.MyViewHolder> {

    private List<MRUEntity> consumerEntities;
    Activity activity;
    private int lastPosition = -1;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, text_con_nmae,text_ac_no,text_con_id,text_book_nocl;
        ImageView go_to_statement_view;
        public MyViewHolder(View view) {
            super(view);
            go_to_statement_view = (ImageView) view.findViewById(R.id.go_to_statement_view);
            text_con_nmae = (TextView) view.findViewById(R.id.text_con_nmae);
            text_ac_no = (TextView) view.findViewById(R.id.text_ac_no);
            text_con_id = (TextView) view.findViewById(R.id.text_con_id);
            text_book_nocl = (TextView) view.findViewById(R.id.text_book_nocl);
            //description = (TextView) view.findViewById(R.id.text_description);
        }
    }


    public UnbilledConsumerAdapter(List<MRUEntity> homeitemEntities, Activity activity) {
        this.consumerEntities = homeitemEntities;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.consumer_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MRUEntity mruEntity = consumerEntities.get(position);
        if (mruEntity.getCNAME().length()>=18)
        holder.text_con_nmae.setText(mruEntity.getCNAME().substring(0,17));
        else holder.text_con_nmae.setText(mruEntity.getCNAME());
        holder.text_ac_no.setText(mruEntity.getACT_NO());
        holder.text_con_id.setText(mruEntity.getCON_ID());
        holder.text_book_nocl.setText(mruEntity.getBOOK_NO());
        holder.go_to_statement_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, PayDetailsActivity.class);
                intent.putExtra("object",mruEntity);
                intent.putExtra("flag","unbilled");
                activity.startActivity(intent);
            }
        });
        //holder.title.setText(homeitemEntities.get(position).getTitle());
        //holder.description.setText(homeitemEntities.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, PayDetailsActivity.class);
                intent.putExtra("flag","unbilled");
                intent.putExtra("object",mruEntity);
                activity.startActivity(intent);
            }
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        //return homeitemEntities.size();
        return consumerEntities.size();
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}