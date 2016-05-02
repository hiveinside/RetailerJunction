package com.example.mridul.RetailerJunction;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by Mridul on 5/2/2016.
 */
public class RetailerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(getApplicationContext());
    }
}
