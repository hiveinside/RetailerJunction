package com.example.mridul.RetailerJunction.utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetailsDao;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppsList extends Application {

    private static List<AppInfoObject> appsList = new ArrayList<AppInfoObject>();
    private final Context context;

    public static List<AppInfoObject> getAppsList() {
        return appsList;
    }

    public AppsList(Context context) {
        this.context = context;

        // incase of reinitializing
        if( appsList.size() > 0 ) {
            appsList.clear();
        }

        {
            // Get appslist from DB - only completed apks
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
            SQLiteDatabase db = helper.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

            List<CloudAppDetails> cd = appEntryDao.loadAll();

            for (int i=0; i<cd.size(); i++) {
                // if downloaded already
                if ((cd.get(i).getApkDownloaded() == true)) {
                    AppInfoObject a = getAppDetail(context, cd.get(i));

                    if(a != null) {
                        appsList.add(a);
                    }
                }
            }
            db.close();
        }
    }


    private AppInfoObject getAppDetail(Context context, CloudAppDetails cloudAppInfo) {

        long campaign_id = cloudAppInfo.getCampaignId();
        String apkfilename = cloudAppInfo.getPackagename() + ".apk";
        String iconfilename = cloudAppInfo.getPackagename() + ".png";

        AppInfoObject appI = new AppInfoObject();

        appI.campaignId = campaign_id;
        appI.appName = cloudAppInfo.getName();
        appI.packageName = cloudAppInfo.getPackagename();

        appI.iconUrl = Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + "/" + iconfilename + "?getIcon";
        appI.apkUrl = Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + "/" + apkfilename + "?getApk";
        appI.version = cloudAppInfo.getVersion();
        appI.checksum = cloudAppInfo.getChecksum();
        appI.size = cloudAppInfo.getSize(); //bytes
        appI.minsdk = cloudAppInfo.getMinsdk(); //min sdk version
        appI.category = cloudAppInfo.getCategory();
        appI.desc = cloudAppInfo.getDesc();
        appI.rating = cloudAppInfo.getRating();

        appI.downloadDone = false; //used by client
        appI.installDone = 0; //used by client
        appI.installts = 0; //used by client

        return appI;
    }

    public String getAppsListJson () {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(appsList);
    }
}
