package dev.altavision.pnrgatewayclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PDUReceiver extends BroadcastReceiver {
    private static final String TAG = "PDU_RCVR";

    private boolean sanityCheck(String result) {
        return result != null && result.contains("REG-RESP");
    }

    private boolean sanityCheck(SmsMessage message) {
        return message != null && sanityCheck(message.getMessageBody());
    }

    private String found(String input) {
        String resdata = input.substring(input.indexOf("REG-RESP"));
        Log.w(TAG, "PDU: " + resdata);
        return resdata;
    }

    private String parse(byte[] pdu) {
        // Print base64-encoded PDU
        Log.w(TAG, "PDU: " + new String(Base64.encode(pdu, Base64.DEFAULT)));
        Log.d(TAG, "Attempting to process message as 3gpp...");
        try {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu, "3gpp");
            if (sanityCheck(message)) {
                return found(message.getMessageBody());
            }
        } catch (Exception e) {
            Log.e(TAG, "The following error occurred while attempting to process PDU as 3gpp:\n" + e.toString());
        }
        Log.d(TAG, "Failed to get body using 3gpp parsing, trying 3gpp2...");
        try {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu, "3gpp2");
            if (sanityCheck(message)) {
                return found(message.getMessageBody());
            }
        } catch (Exception e) {
            Log.e(TAG, "The following error occurred while attempting to process PDU as 3gpp2:\n" + e.toString());
        }
        Log.d(TAG, "Failed to get body using 3gpp2 parsing, manually parsing...");
        try {
            String pduString = new String(pdu, 0, pdu.length, StandardCharsets.US_ASCII);
            if (sanityCheck(pduString)) {
                return found(pduString);
            }
        } catch (Exception e) {
            Log.e(TAG, "The following error occurred while attempting to process PDU manually:\n" + e.toString());
        }
        Log.w(TAG, "PDU could not be deciphered.");
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Runs whenever a data SMS PDU is received. On some carriers (i.e. AT&T), the REG-RESP message is sent as
        //  a data SMS (PDU) instead of a regular SMS message.
        Log.d(TAG, "Received intent!");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            Log.d(TAG, "Got "+pdus.length+" PDUs");

            for (Object o : pdus) {
                SMSReceiver.processMessage(parse((byte[]) o));
                //SMSReceiver.processMessage(SmsMessage.createFromPdu((byte[]) pdus[i]));
            }
        }
    }
}
