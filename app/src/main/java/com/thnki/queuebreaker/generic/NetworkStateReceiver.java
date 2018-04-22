package com.thnki.queuebreaker.generic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

    public static final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CONNECTIVITY_CHANGE.equals(intent.getAction())) {
            Log.d("CONNECTIVITY_CHANGE", intent.getAction());
            Intent activityIntent = new Intent(context, NetworkStateActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.putExtra(NetworkStateActivity.NETWORK_STATUS, isConnected(context));
            context.startActivity(activityIntent);
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                Log.d("CONNECTIVITY_CHANGE", "Connected");
                return true;
            }
        }
        Log.d("CONNECTIVITY_CHANGE", "disconnected");
        return false;
    }


}
