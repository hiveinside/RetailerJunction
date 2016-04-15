package com.example.mridul.RetailerJunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mridul.helloworld.R;

import java.io.IOException;

/**
 * Created by satish on 4/12/16.
 */
public class HotspotFragment extends Fragment {
    private static final int TURN_ON_HOTSPOT = 0;
    private static final int TURNING_ON_HOTSPOT = 1;
    private static final int TURN_OFF_HOTSPOT = 2;
    private static final int TURNING_OFF_HOTSPOT = 3;
    private static final int HOTSPOT_ERROR = 4;

    httpServer h;
    static WifiManager mWifiManager;
    static WifiConfiguration wifiConfiguration;
    WifiApControl apMgr;

    static Button button;
    static TextView ssidText;
    static TextView ipText;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_hotspot, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = (Button) getActivity().findViewById(R.id.HotSpotButton);
        ssidText = (TextView) getActivity().findViewById(R.id.hotspotdetails);
        ipText = (TextView) getActivity().findViewById(R.id.ipdetails);


        context = getActivity().getApplicationContext();

        button.setTag(TURN_OFF_HOTSPOT);

        //button.setOnClickListener(this);


        IntentFilter mIntentFilter=new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        getActivity().registerReceiver(new WifiReceiver(), mIntentFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                apMgr = WifiApControl.getApControl(mWifiManager, context);
                Integer tag = (Integer)button.getTag();
                if (button.getTag() == TURN_OFF_HOTSPOT) {

                    // Parse APKs. Store data.
                    AppsList localappsList = new AppsList(context);

                    //Start hotspot - without user config.
                    boolean result = apMgr.enable(false, context);
                    if (result == true) {

                        wifiConfiguration = apMgr.getWifiApConfiguration();
                        // Start webserver
                        try {
                            h = new httpServer(context);
                        } catch (IOException ioe) {
                            button.setTag(TURN_OFF_HOTSPOT);
                            updateButtonText("HTTP server failed. Retry");
                            h.stop();
                        }
                    } else {
                        button.setTag(TURN_OFF_HOTSPOT);
                        wifiConfiguration = null;
                        updateButtonText("HotSpot failed. Retry");
                    }

                } else if (button.getTag() == TURN_ON_HOTSPOT) {
                    h.stop(); // Stop hotspot
                    apMgr.disable(); // Stop webserver
                    wifiConfiguration = null;
                }
            }
        });
    }
    public static class WifiReceiver extends BroadcastReceiver {

        //ConnectionMonitor
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            String msg = null;

            switch (state) {
                case 10: //WifiManager.WIFI_STATE_DISABLING:
                    button.setTag(TURNING_OFF_HOTSPOT);
                    msg = "Turning OFF...";
                    break;
                case 11: //WifiManager.WIFI_STATE_DISABLED:
                    button.setTag(TURN_OFF_HOTSPOT);
                    msg = "Turn ON HotSpot";
                    break;
                case 12: //WifiManager.WIFI_STATE_ENABLING:
                    button.setTag(TURNING_ON_HOTSPOT);
                    msg = "Turning ON...";
                    break;
                case 13: //WifiManager.WIFI_STATE_ENABLED:
                    button.setTag(TURN_ON_HOTSPOT);
                    msg = "Turn OFF HotSpot";
                    break;
                default:
                    button.setTag(HOTSPOT_ERROR);
                    msg = "HOTSPOT_ERROR";
                    break;
            }
            updateButtonText(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stop everything
        if (h != null)
            h.stop(); // Stop hotspot

        if (apMgr!= null)
            apMgr.disable(); // Stop webserver
    }

    public static void updateButtonText(String msg) {
        //Button button = (Button)findViewById(R.id.HotSpot);
        if (button != null)
            button.setText(msg);

        // hotspot text

        if (wifiConfiguration != null) {
            ssidText.setText("HotSpot: " + wifiConfiguration.SSID);
        } else {
            ssidText.setText("HotSpot: " + "");
        }

        //ipText.setText(Formatter.formatIpAddress(mWifiManager.getConnectionInfo().getIpAddress()));
        ipText.setText("IP Address: " + "192.168.43.1" + ":" + Constants.HTTP_PORT);
    }
}

