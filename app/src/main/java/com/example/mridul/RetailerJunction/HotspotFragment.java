package com.example.mridul.RetailerJunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mridul.helloworld.R;

import java.io.IOException;

/**
 * Created by satish on 4/12/16.
 */
public class HotspotFragment extends Fragment {
    Button button;
    Context context;
    static httpServer h;

    private BroadcastReceiver mBroadcastReceiver;
    private static final int TURN_ON_HOTSPOT = 0;
    private static final int TURNING_ON_HOTSPOT = 1;
    private static final int TURN_OFF_HOTSPOT = 2;
    private static final int TURNING_OFF_HOTSPOT = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_hotspot, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = (Button) getActivity().findViewById(R.id.HotSpot);
        context = getActivity().getApplicationContext();

        button.setTag(TURN_ON_HOTSPOT); //default
        //button.setOnClickListener(this);


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                String msg = null;
                Log.d("Saty","WiFi state::"+state);

                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        msg = "it is disabled";
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        msg = "it is enabled";
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        msg = "it is switching off";
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        msg = "wifi is getting enabled";
                        break;
                    default:
                        msg = "not working properly";
                        break;
                }
                if (msg != null) {
                    Log.d("Saty","Exception occured");
                }
            }
        };

        IntentFilter mIntentFilter=new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Context context = getActivity().getApplicationContext();
                WifiManager mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                final WifiApControl apMgr = WifiApControl.getApControl(mWifiManager, context);

                if (button.getTag() == TURN_ON_HOTSPOT) {

                    // Parse APKs. Store data.
                    AppsList localappsList = new AppsList(context);

                    updateButtonText("Turning ON...");
                    button.setTag(TURNING_ON_HOTSPOT);

                    apMgr.enable(false, context);
                    // Start webserver
                    try {
                        h = new httpServer(context);
                    } catch (IOException ioe) {
                        updateButtonText("Failed to start HotSpot");
                        button.setTag(TURN_ON_HOTSPOT);
                    }
                    updateButtonText("Turn OFF HotSpot");
                    button.setTag(TURN_OFF_HOTSPOT);
                } else if (button.getTag() == TURNING_ON_HOTSPOT) {

                    // Do nothing

                } else if (button.getTag() == TURN_OFF_HOTSPOT) {

                    updateButtonText("Turning OFF...");
                    button.setTag(TURNING_OFF_HOTSPOT);

                    h.stop(); // Stop hotspot
                    apMgr.disable(); // Stop webserver

                    updateButtonText("Turn ON Hotspot");
                    button.setTag(TURN_ON_HOTSPOT);
                } else if (button.getTag() == TURNING_OFF_HOTSPOT) {

                    // Do nothing

                } else {
                    // Do nothing
                    updateButtonText("Error");
                    button.setTag(TURN_ON_HOTSPOT);
                }
            }
        });
    }


    public void updateButtonText(String msg) {
        //Button button = (Button)findViewById(R.id.HotSpot);
        button.setText(msg);
    }
}

