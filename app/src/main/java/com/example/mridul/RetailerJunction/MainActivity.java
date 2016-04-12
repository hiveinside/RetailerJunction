package com.example.mridul.RetailerJunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mridul.helloworld.R;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private BroadcastReceiver mBroadcastReceiver;

    private static final int TURN_ON_HOTSPOT = 0;
    private static final int TURNING_ON_HOTSPOT = 1;
    private static final int TURN_OFF_HOTSPOT = 2;
    private static final int TURNING_OFF_HOTSPOT = 3;
    static httpServer h;
    Button button;
    Context context;

    void ShowToast (String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.HotSpot);
        context = getApplicationContext();

        button.setTag(TURN_ON_HOTSPOT); //default
        //button.setOnClickListener(this);


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                String msg = null;
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
                    Toast.makeText(context, "Wifi state is" + msg, Toast.LENGTH_LONG)
                            .show();
                }
            }
        };
    }

    public void updateButtonText(String msg) {
        //Button button = (Button)findViewById(R.id.HotSpot);
        button.setText(msg);
    }

    public void installApps(View v) {
        final Context context = getApplicationContext();
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
            }
            catch (IOException ioe ) {
                updateButtonText("Failed to start HotSpot");
                button.setTag(TURN_ON_HOTSPOT);
            }
            updateButtonText( "Turn OFF HotSpot" );
            button.setTag(TURN_OFF_HOTSPOT);
        } else if (button.getTag() == TURNING_ON_HOTSPOT){

            // Do nothing

        } else if (button.getTag() == TURN_OFF_HOTSPOT) {

            updateButtonText( "Turning OFF..." );
            button.setTag(TURNING_OFF_HOTSPOT);

            h.stop(); // Stop hotspot
            apMgr.disable(); // Stop webserver

            updateButtonText("Turn ON Hotspot");
            button.setTag(TURN_ON_HOTSPOT);
        } else if (button.getTag() == TURNING_OFF_HOTSPOT) {

            // Do nothing

        } else {
            // Do nothing
            updateButtonText("Error" );
            button.setTag(TURN_ON_HOTSPOT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
