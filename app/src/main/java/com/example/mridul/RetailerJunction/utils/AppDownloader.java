package com.example.mridul.RetailerJunction.utils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetailsDao;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.ui.OfflineAppsFragment;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mridul on 4/17/2016.
 */
public class AppDownloader {

    private static final String TAG = "AppDownloader";
    //private static final String DESTINATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/update.apk";
    private static String APK_DIR = "final_dir";
    OfflineAppsFragment fragment;
    List<CloudAppDetails> downloadList;

    public AppDownloader(OfflineAppsFragment offlineAppsFragment) {
        fragment = offlineAppsFragment;
    }

    public void download(String ROOT_DIR, List<CloudAppDetails> dList) {

        APK_DIR = ROOT_DIR + "/apks/";
        this.downloadList = dList;

        /*
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

        List<CloudAppDetails> cd = appEntryDao.loadAll();
        */

        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(mFileDownloadListener);

        final List<BaseDownloadTask> tasks = new ArrayList<>();
        for (int i = 0; i < downloadList.size(); i++) {
            final String link = downloadList.get(i).getDownloadurl();
            final String destFile = APK_DIR + downloadList.get(i).getPackagename() + ".tmp"; // dont add .apk till completed + ".apk";
            Log.d(TAG, "download link : " + link + " " + destFile);

            tasks.add(FileDownloader.getImpl().create(link).setPath(destFile).setListener(mFileDownloadListener).setTag(downloadList.get(i)));
        }
        //queueSet.disableCallbackProgressTimes(); // do not want each task's download progress's callback,
        // we just consider which task will completed.

        // auto retry 1 time if download fail
        queueSet.setAutoRetryTimes(1);

        // start download in serial order
        queueSet.downloadSequentially(tasks);

        queueSet.start();
    }

    final FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download pending : " + task.getPath() + " sofar " + soFarBytes + " TotalBytes : " + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            //Log.d(TAG, "download progress : " + task.getPath() + " sofar " + soFarBytes + " TotalBytes : " + totalBytes);
            fragment.onApkDownloadProgress(task, soFarBytes, totalBytes);

        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Log.d(TAG, "download blockComplete : " + task.getPath());
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.d(TAG, "download completed : " + task.getPath());

            // match checksum, delete if doesnt match
            // match with what was sent from cloud
            // // TODO: 5/10/2016 match checksum

            // rename to .apk
            String currentName = task.getPath();
            File from = new File(currentName);
            File to = new File(FilenameUtils.removeExtension(currentName) + ".apk");

            from.renameTo(to);
            //FileUtils.copyFile(from, to);

            // update database - mark downloaded, apkts
            // write to database
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

            //get record
            long campaignId = ((CloudAppDetails) task.getTag()).getCampaignId();
            CloudAppDetails c = appEntryDao.queryBuilder().where(CloudAppDetailsDao.Properties.CampaignId.eq(campaignId)).limit(1).unique();

            if (c != null) {
                c.setDownloaded(true);
                c.setApkts(System.currentTimeMillis());

                appEntryDao.insertOrReplace(c);
            }
            db.close();
            // Update local data structures -- appsList

            // refresh UI
            fragment.onApkDownloadCompleted(task);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.d(TAG, "download paused : " + task.getPath() + " sofar " + soFarBytes + " TotalBytes : " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.d(TAG, "download error : " + task.getPath() + " Exception " + e);

            // refresh UI
            fragment.onApkDownloadError(task);

            // pause/stop rest of the queue
            FileDownloader.getImpl().pause(mFileDownloadListener);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.d(TAG, "download warn : " + task.getPath());
        }
    };

    public interface AppDownloadCallback {
        public void onApkDownloadCompleted(BaseDownloadTask task);

        public void onApkDownloadError(BaseDownloadTask task);

        public void onApkDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes);
    }
}
