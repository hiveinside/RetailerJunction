package com.example.mridul.RetailerJunction.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.http.httpServer;
import com.example.mridul.RetailerJunction.utils.AppsList;
import com.example.mridul.RetailerJunction.utils.LoginUtils;
import com.example.mridul.helloworld.R;

public class RetailerJunctionActivity extends FragmentActivity implements ActionBar.TabListener {

    private BroadcastReceiver mBroadcastReceiver;

    private static final int TURN_ON_HOTSPOT = 0;
    private static final int TURNING_ON_HOTSPOT = 1;
    private static final int TURN_OFF_HOTSPOT = 2;
    private static final int TURNING_OFF_HOTSPOT = 3;
    static httpServer h;
    Button button;

    void ShowToast (String text) {
        Toast.makeText(RetailerJunctionActivity.this, text, Toast.LENGTH_SHORT).show();
    }
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private final String[] tabs = { "Hotspot", "Sync", "Console" };
    private final int[] tabIcons = new int[] {
            R.drawable.wifi,
            R.drawable.wifi,
            R.drawable.wifi
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (LoginUtils.isLoggedIn(this) == false) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        RetailerApplication.setRJContext(this);
        RetailerApplication.setIconDir(getApplicationContext().getFilesDir().getAbsolutePath() + "/icons/");
        RetailerApplication.setApkDir(getApplicationContext().getFilesDir().getAbsolutePath() + "/apks/");
        setContentView(R.layout.activity_main);

        // Parse APKs. Store data.
        // initialize appsList
        // // TODO: 5/7/2016
        new AppsList(this);

        // Initilization
        actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);  // hides action bar icon
        actionBar.setDisplayShowTitleEnabled(false); // hides action bar title
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mAdapter);

        // Adding Tabs
        for (int i=0; i<tabs.length; i++) {
            actionBar.addTab(actionBar.newTab().setText(tabs[i])
                    //.setIcon(getResources().getDrawable(tabIcons[i]))
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
