package com.chekoff.hackafeforum;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class NetworkStatus {

    private static NetworkStatus instance = new NetworkStatus();
    static Context context;
    ConnectivityManager connectivityManager;

    public static NetworkStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public String isOnline() {
        String result = "NO_CONNECTION";
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI"))
                result = "WIFI";
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                result = "MOBILE";

        } catch (Exception e) {
            System.out.println("Check Connectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
            result = "NO_CONNECTION";
        }
        if (result == "NO_CONNECTION")
            Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show();
        return result;
    }
}
