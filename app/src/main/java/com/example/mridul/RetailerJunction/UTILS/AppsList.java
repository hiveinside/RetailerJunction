package com.example.mridul.RetailerJunction.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FilenameUtils;

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

        if( appsList.size() > 0 ) {
            appsList.clear();
        }

        {
            // load list
            String STORAGE_DIRECTORY = context.getApplicationContext().getFilesDir().getAbsolutePath();
            String path = STORAGE_DIRECTORY + "/apks/";

            //AssetManager assetManager = getAssets();
            File f = new File(path);
            File file[] = f.listFiles();

            if (file == null) {
                // no apks
                return;
            }
            //String list[] = assetManager.list(path);
            Log.d("Files", "Size: " + file.length);

            for (int i = 0; i < file.length; i++) {
                // check only apks
                if( FilenameUtils.getExtension(file[i].getName()).equals("apk")) {
                    Log.e("Files", "Filename: " + file[i].getName());
                    appsList.add(getAppDetail(context, path + file[i].getName()));
                }
            }
        }
    }


    private AppInfoObject getAppDetail(Context context, String APKFilePath) {


        AppInfoObject appI = new AppInfoObject();

        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

        // the secret are these two lines....
        pi.applicationInfo.sourceDir       = APKFilePath;
        pi.applicationInfo.publicSourceDir = APKFilePath;
        //

        Drawable d = pi.applicationInfo.loadIcon(pm);
        appI.AppName = (String)pi.applicationInfo.loadLabel(pm);
        appI.packageName = (String)pi.applicationInfo.packageName;
        appI.iconUrl = storeDrawable(d, (String)pi.applicationInfo.packageName);
        appI.apkUrl = Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + APKFilePath + "?getFile";
     /*  // String encodedPath = "";
        try {
            appI.apkUrl = URLEncoder.encode(appI.apkUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //appI.apkUrl = Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + encodedPath;*/


        return appI;
    }


    private String storeDrawable (Drawable drawable, String packageName) {
        Bitmap bm = drawableToBitmap(drawable);

        String STORAGE_DIRECTORY = context.getApplicationContext().getFilesDir().getAbsolutePath();
        String iconStorageDirectory = STORAGE_DIRECTORY + "/icons/"; //dont add extra slash

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

        return Constants.DEFAULT_IP_ADDRESS + ":" + Constants.HTTP_PORT + iconStorageDirectory + packageName + ".PNG" + "?getFile";
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
