package com.bih.nic.e_wallet.broadcastRecever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bih.nic.e_wallet.asynkTask.PendingTransCountLoader;

public class PendingTransactionRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Alarm called"+intent.getStringExtra("time"), Toast.LENGTH_SHORT).show();
        new PendingTransCountLoader(context).execute();
    }
}