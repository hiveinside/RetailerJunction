package com.example.mridul.RetailerJunction.WiFi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class WifiReceiver_old extends BroadcastReceiver {

    //ConnectionMonitor
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        String msg = null;

        switch (state) {
            case 10: //WifiManager.WIFI_STATE_DISABLING:
                msg = "DISABLING";
                break;
            case 11: //WifiManager.WIFI_STATE_DISABLED:
                msg = "DISABLED";
                break;
            case 12: //WifiManager.WIFI_STATE_ENABLING:
                msg = "ENABLING";
                break;
            case 13: //WifiManager.WIFI_STATE_ENABLED:
                msg = "ENABLED";
                break;
            default:
                msg = "not working properly";
                break;
        }
        if (msg != null) {
            Log.d("WifiReceiver", msg);
        }
    }
}
