package com.example.mridul.RetailerJunction.ui;

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

    static RetailerJunction mRetailerJunction;
    public static RetailerJunction getRJContext(){
        return mRetailerJunction;
    }
    public static void setRJContext(RetailerJunction rj) {
        mRetailerJunction = rj;
    }

    static String iconDir;
    public static void setIconDir(String path) {
        iconDir = path;
    }
    public static String getIconDir() {
        return iconDir;
    }

    static String apkDir;
    public static void setApkDir(String path) {
        apkDir = path;
    }
    public static String getApkDir() {
        return apkDir;
    }

}
