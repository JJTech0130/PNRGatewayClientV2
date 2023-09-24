package dev.altavision.pnrgatewayclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;
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

        apiServer = new APIServer(this); // Start the API server

        //Implement displaying the IP address
        String ips = "\n";
        ConnectivityManager cm = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        List<LinkAddress> las = cm.getLinkProperties(cm.getActiveNetwork()).getLinkAddresses();
        for(LinkAddress la : las){
            try {
                ips += ((Inet4Address) la.getAddress()).getHostAddress() + "\n";
            } catch (Exception ig) {
                Log.e(TAG, ig.toString());
            }
        }
        TextView ip = (TextView)findViewById(R.id.ip);
        ip.setText(ips);
    }

    public void smsPerms(View view) {
        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS);
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    showToast("Awesome!  I can now send and receive SMS!");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    showToast("You denied, you silly goose!  Now you'll have to grant in settings.");
                }
            });

    public void showToast(String textstr) {
        CharSequence text = textstr;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
        toast.show();
    }

}