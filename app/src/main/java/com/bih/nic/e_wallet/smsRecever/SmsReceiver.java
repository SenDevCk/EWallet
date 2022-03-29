package com.bih.nic.e_wallet.smsRecever;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by NIC2 on 1/6/2018.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    //@TargetApi()
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                //Log.d("sms_ewallet","Sender :- "+sender+"\n"+message);
                if (sender.contains("SBPDCL")||sender.contains("NBPDCL")) {
                    //Log.d("sms_ewallet_under","Sender :- "+sender+"\n"+message);
                    //Pass on the text to our listener.
                    mListener.messageReceived(message);
                }
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }


    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
