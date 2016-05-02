package com.example.mridul.RetailerJunction.utils;

import android.os.Environment;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

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
    private static final String DOWNLOAD_LINK = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk";
    //private static final String DESTINATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/update.apk";
    private static String ROOT_DIR = "destination_dir";
    private static final String DESTINATION = "/apks/redbus1.apk";

    public void download(String dest) {
        final String link = DOWNLOAD_LINK;
        ROOT_DIR = dest;
        Log.d(TAG, "download link : "+link + " "+ ROOT_DIR + DESTINATION);
        FileDownloader.getImpl().create(link).setPath(ROOT_DIR + DESTINATION).setListener(mFileDownloadListener).start();
    }

    final FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download pending : "+task +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download progress : "+task +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Log.d(TAG, "download blockComplete : "+task);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.d(TAG, "download completed : "+task);

            // match checksum, delete if doesnt match
            // move to final directory
            // remove from pending download list
            // refresh UI
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download paused : "+task +" sofar "+soFarBytes + " TotalBytes : "+totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.d(TAG, "download error : " + task + " Exception " + e);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.d(TAG, "download warn : "+task);
        }
    };
}
