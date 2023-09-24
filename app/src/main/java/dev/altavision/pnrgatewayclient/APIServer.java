package dev.altavision.pnrgatewayclient;

import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;

public class APIServer extends NanoHTTPD {

        private static final String TAG = "API_SERVER";

        public static List<String> incomingMessages = new ArrayList<>(); // TODO: Does this have any thread-safety issues?

        public static List<String> gatewayAddresses = new ArrayList<>(); // List of gateway addresses we are currently expecting messages from

        private Context context;

        // Singleton
//        private static APIServer mInstance = null;
//
//        public static APIServer getInstance() {
//            if(mInstance == null) {
//                try {
//                    mInstance = new APIServer();
//                } catch (IOException e) {
//                    Log.e(TAG, "API server failed to start: " + e.getMessage());
//                }
//            }
//            return mInstance;
//        }

        public APIServer(Context context) {
            super(8080);
            try {
                start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            } catch (IOException e) {
                Log.e(TAG, "API server failed to start: " + e.getMessage());
            }
            this.context = context;
            Log.d(TAG, "API Server started on port 8080");
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            Log.d(TAG, "URI: " + uri);

            // Return a version string for the root path
            if (uri.equals("/")) {
                return newFixedLengthResponse("PnrGatewayClient API Server v0.0.1");
            }
            if (uri.equals("/info")) {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String carrierMccMnc = manager.getNetworkOperator();
                return newFixedLengthResponse(carrierMccMnc);
            }
            // Main register endpoint
            if (uri.equals("/register")) {
                // Get the parameters from the request
                Map<String, List<String>> paramsMap = session.getParameters();

                // Get the endpoint phone number
                List<String> gatewayParameters = paramsMap.get("gateway");
                if (gatewayParameters == null) {
                    return newFixedLengthResponse("Error: gateway parameter not found");
                }
                String gatewayPhoneNumber = gatewayParameters.get(0);

                // Get the SMS to send
                List<String> smsParameters = paramsMap.get("sms");
                if (smsParameters == null) {
                    return newFixedLengthResponse("Error: sms parameter not found");
                }
                String smsToSend = smsParameters.get(0);


                Log.d(TAG, "Sending registration request to gateway " + gatewayPhoneNumber + ": " + smsToSend);

                // Build the content of the REG-REQ SMS
                //String requestMessage = "REG-REQ?v=3;t="+pushToken+";r="+requestId;

                //Log.d(TAG, "Request message to send: " + requestMessage);

                SmsManager smsManager = SmsManager.getDefault();
                // Catch the exception if the SMS fails to send
                try {
                    smsManager.sendTextMessage(gatewayPhoneNumber, null, smsToSend, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "Error sending SMS: " + e.getMessage());
                    return newFixedLengthResponse("Error sending SMS: " + e.getMessage());
                }

                APIServer.gatewayAddresses.add(gatewayPhoneNumber);

                String acceptableResponse = null;
                int timeout = 150;

                while (acceptableResponse == null && timeout > 0) {
                    timeout--;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Error sleeping: " + e.getMessage());
                    }
                    for (String message : APIServer.incomingMessages) {
                        if (message.contains("REG-RESP")) { // Basic sanity check to make sure we're getting a REG-RESP message
                            acceptableResponse = message;
                            APIServer.incomingMessages.remove(message);
                            break;
                        }
                    }
                }

                if (acceptableResponse == null) {
                    return newFixedLengthResponse("Error: timeout waiting for response from gateway");
                }

                // Return the response from the gateway
                return newFixedLengthResponse(acceptableResponse);
            }


            // Return a 404 for all other requests
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
        }
}
