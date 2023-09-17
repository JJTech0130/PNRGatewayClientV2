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

    private String parse(byte[] pdu) {
        // Print base64-encoded PDU
        Log.w(TAG, "PDU: " + new String(Base64.encode(pdu, Base64.DEFAULT)));
//        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu, "3gpp");
//        if (message.getMessageBody() == null) {
//            Log.d(TAG, "Failed to get body using 3gpp parsing, trying 3gpp2...");
//            message = SmsMessage.createFromPdu((byte[]) pdu, "3gpp2");
//            if (message.getMessageBody() == null) {
//                Log.d(TAG, "Failed to get body using 3gpp2 parsing, manually parsing...");
//                // Remove non-ASCII characters from the PDU
        String pduString = new String(pdu, 0, pdu.length, StandardCharsets.US_ASCII);
        // Extract the message body from the PDU
        int index = pduString.indexOf("REG-RESP");
        if (index == -1) {
            Log.w(TAG, "Failed to get body using manual parsing, ignoring...");
            Log.w(TAG, "PDU: " + pduString);
            return null;
        }
        String messageBody = pduString.substring(index);
        Log.w(TAG, "Got message body using manual parsing: " + messageBody);
        return messageBody;
//            }
//        }
//        Log.d(TAG, "Got message body using 3gpp/3gpp2 parsing: " + message.getMessageBody());
//        // Check for REG-RESP message
//        if (!message.getMessageBody().contains("REG-RESP")) {
//            Log.d(TAG, "Got message that is not a REG-RESP, ignoring...");
//            return null;
//        }
//        return message.getMessageBody();
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
