package com.example.mridul.RetailerJunction.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
                if ((cd.get(i).getDownloaded() == true)) {
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
        String filename = cloudAppInfo.getPackagename() + ".apk";

        String APKFilePath = RetailerApplication.getApkDir() + filename;

        AppInfoObject appI = new AppInfoObject();

        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

        if (pi == null) {
            // Trying to parse non existent file.
            // update database
            // // TODO: 5/10/2016 fix database that file was not downloaded
            // for now it will still work as at app start appsList will not have this apk
            // downloader will download.

            return null;
        }

        // the secret are these two lines....
        pi.applicationInfo.sourceDir       = APKFilePath;
        pi.applicationInfo.publicSourceDir = APKFilePath;
        //

        Drawable d = pi.applicationInfo.loadIcon(pm);
        appI.campaignId = campaign_id;
        appI.appName = (String)pi.applicationInfo.loadLabel(pm);
        appI.packageName = (String)pi.applicationInfo.packageName;

        appI.iconUrl = storeDrawable(d, (String)pi.applicationInfo.packageName);
        appI.apkUrl = Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + "/" + filename + "?getApk";
        appI.version = cloudAppInfo.getVersion();
        appI.checksum = cloudAppInfo.getChecksum();
        appI.size = cloudAppInfo.getSize(); //bytes

        /*
            Date date;
            Float installPrice;
            Float activatePrice;
         */

        return appI;
    }


    private String storeDrawable (Drawable drawable, String packageName) {
        Bitmap bm = drawableToBitmap(drawable);

        String iconStorageDirectory = RetailerApplication.getIconDir(); //dont add extra slash

        File myFile = new File(iconStorageDirectory, packageName + ".PNG");

        if (!myFile.exists()) {
            myFile.getParentFile().mkdirs();
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(myFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + "/" + packageName + ".PNG" + "?getIcon";
    }


    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public String getAppsListJson () {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(appsList);
    }
}
