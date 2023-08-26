package dev.altavision.pnrgatewayclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class PDUReceiver extends BroadcastReceiver {
    private static final String TAG = "PDU_RCVR";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Runs whenever a data SMS PDU is received. On some carriers (i.e. AT&T), the REG-RESP message is sent as
        //  a data SMS (PDU) instead of a regular SMS message.
        Log.d(TAG, "Received intent!");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            Log.d(TAG, "Got "+pdus.length+" PDUs");

            for (int i = 0; i < pdus.length; i++) {
                SMSReceiver.processMessage(SmsMessage.createFromPdu((byte[]) pdus[i]));
            }
        }
    }
}
