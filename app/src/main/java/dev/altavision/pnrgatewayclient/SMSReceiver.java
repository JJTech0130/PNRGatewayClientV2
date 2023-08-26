package dev.altavision.pnrgatewayclient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMS_RCVR";

    public static void processMessage(SmsMessage message) {
        // Called by both the PDUReceiver and the SMSReceiver to process an incoming message
        //  and notify the user if it's a REG-REQ message
        String sender = message.getOriginatingAddress();

        // Check if the sender is in the list of allowed senders
        if (!APIServer.gatewayAddresses.contains(sender)) {
            Log.d(TAG,"Got message from sender "+sender+", not in allowed senders list, ignoring...");
            return;
        }

        String messageBody = message.getMessageBody();

        Log.d(TAG,"Got message from sender "+sender+", processing...");
        APIServer.incomingMessages.add(messageBody);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //This runs whenever a regular SMS is received--i.e. to capture the incoming REG-REQ message from the iPhone so
        //  we can notify the user. The user should then paste the REG-RESP contents into the ReceivePNR command
        //  on the iPhone via SSH
        Log.d(TAG, "Received intent!");

        SmsMessage[] extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (int i = 0; i < extractMessages.length; i++) {
            SmsMessage message = extractMessages[i];
            processMessage(message);
        }

    }
}
