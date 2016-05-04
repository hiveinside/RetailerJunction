package com.example.mridul.RetailerJunction.utils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetailsDao;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mridul on 4/17/2016.
 */
public class AppDownloader {

    /*
    id,
    appid,
    totalbytes,
    downloadedbytes,
    package,
    md5
     */
    private static final String TAG = "AppDownloader";
    //private static final String DESTINATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/update.apk";
    private static String ROOT_DIR = "destination_dir";
    private static String APK_DIR = "final_dir";

    public void download(String rootDir) {

        ROOT_DIR = rootDir;
        APK_DIR = ROOT_DIR + "/apks/";


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

        List<CloudAppDetails> cd = appEntryDao.loadAll();

        for( int i=0; i< cd.size(); i++) {
            Log.d(TAG, cd.get(i).getDownloadurl());

            final String link = cd.get(i).getDownloadurl();
            final String destFile = APK_DIR + cd.get(i).getPackagename() + ".tmp"; // dont add .apk till completed + ".apk";
            Log.d(TAG, "download link : " + link + " " + destFile);
            FileDownloader.getImpl().create(link).setPath(destFile).setListener(mFileDownloadListener).start();
        }
    }

    final FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download pending : "+task.getPath() +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download progress : "+task.getPath() +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Log.d(TAG, "download blockComplete : "+task.getPath());
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.d(TAG, "download completed : "+task.getPath());

            // match checksum, delete if doesnt match

            // rename to .apk
            String currentName = task.getPath();
            File from = new File(currentName);
            File to = new File(currentName + ".apk");

            try {
                FileUtils.copyFile(from, to);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // remove from database - pending download list
            // add to database - offline apps list
            // Update local data structures -- appsList
            // refresh UI
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download paused : "+task.getPath() +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.d(TAG, "download error : " + task.getPath() + " Exception " + e);

            // decide how to restart in failed cases.
            // restart when network in available


            // Update local data structures -- appsList
            // update Database
            // refresh UI


        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.d(TAG, "download warn : "+task.getPath());
        }
    };
}
