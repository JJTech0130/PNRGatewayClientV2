package dev.altavision.pnrgatewayclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACT";
    private SharedPreferences mPrefs;

    APIServer apiServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        apiServer = new APIServer(); // Start the API server

//        ((Button) findViewById(R.id.send_req_sms_button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//
//                    Log.d(TAG, mPrefs.getString("gateway_address","none"));
//
//                    if (mPrefs.getString("gateway_address","").trim().equals("")) {
//                        Toast.makeText(MainActivity.this, "Error: Please set the gateway address in Settings", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    //Extracts the push token from the log message
//                    String rawTokenMessage = ((TextView) findViewById(R.id.push_token_field)).getText().toString();
//                    String regex = "\\<([a-f0-9\\ ]+)\\>";
//
//                    Pattern pattern = Pattern.compile(regex);
//                    Matcher matcher = pattern.matcher(rawTokenMessage);
//
//                    if (matcher.find()) {
//
//                        String pushTokenCleaned = matcher.group(1).replaceAll("\\s", "").toUpperCase(); //Cleans up the push token so it's in the all-caps, no-spaces format
//
//                        //Generates a random request (r=) number
//                        Random random = new Random();
//                        long randomNum = (long) (random.nextDouble() * 10000000000L);
//
//                        //Builds the content of the REG-REQ SMS
//                        String smsToSend = "REG-REQ?v=3;t="+pushTokenCleaned+";r="+randomNum;
//
//                        //Sends the SMS to the gateway address
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(mPrefs.getString("gateway_address","none"), null, smsToSend, null, null);
//
//                    } else {
//                        Log.w(TAG,"Match not found.");
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        //Opens the settings screen on button click
//        ((Button) findViewById(R.id.settingsButton)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//
//                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                    startActivity(intent);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

}