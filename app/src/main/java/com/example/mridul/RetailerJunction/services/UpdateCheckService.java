package com.example.mridul.RetailerJunction.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.example.mridul.RetailerJunction.utils.ApkDownloader;
import com.example.mridul.RetailerJunction.utils.AppDownloader;
import com.example.mridul.RetailerJunction.utils.CloudSelfUpdateObject;
import com.example.mridul.RetailerJunction.utils.Consts;
import com.example.mridul.helloworld.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pankaj on 19/6/16.
 */
public class UpdateCheckService extends Service {

    private final String TAG = "UpdateCheckService";
    private Handler mHandler;

    private Notification.Builder builder;
    private NotificationManager manager;
    private AppDownloader appDownloader;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (Consts.CHECK_UPDATE.equals(intent.getAction())) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUpdate();
                    }
                }, 500);
            }
            else if (Consts.DOWNLOAD_UPDATE.equals(intent.getAction())) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadUpdate();
                    }
                }, 500);
            }
            else if (Consts.INSTALL_UPDATE.equals(intent.getAction())) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUpdate();
                    }
                }, 500);
            }
        }
        return START_STICKY;
    }

    private void checkUpdate() {
        FetchInfoTask fetchInfoTask = new FetchInfoTask();
        String url = Consts.CHECK_UPDATE_URL + PreferencesHelper.getInstance(UpdateCheckService.this).getToken();
        fetchInfoTask.execute(url);
    }

    private void downloadUpdate() {
        String selfUpdateInfo = PreferencesHelper.getInstance(getApplicationContext())
                .getSelfUpdateInfo();
        if (selfUpdateInfo == null || "".equals(selfUpdateInfo)) {
            return;
        }
        Type listType = new TypeToken<List<CloudSelfUpdateObject>>() {}.getType();
        Gson gson = new Gson();
        final List<CloudSelfUpdateObject> data = gson.fromJson(selfUpdateInfo, listType);
        if(data == null || data.isEmpty()) {
            return;
        }

        CloudSelfUpdateObject app = data.get(0);
        CloudAppDetails cloudAppDetails = new CloudAppDetails(
                0l, // campaignId
                getApplicationInfo().name, // name
                app.versionstring, // version
                app.size, // size
                app.url, // downloadurl
                getPackageName(), // packagename
                app.checksum, // checksum
                0, // state
                Build.VERSION.SDK_INT, // minsdk
                0l, // listts
                false, // downloaded
                0l // apkts
        );
        cloudAppDetails.setIsSelfUpdate(true);
        appDownloader = new AppDownloader(new AppDownloader.AppDownloadCallback() {
            @Override
            public void onApkDownloadCompleted(BaseDownloadTask task) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInstallNotification();
                    }
                }, 50);
            }

            @Override
            public void onApkDownloadError(BaseDownloadTask task) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateCheckService.this, "Self update download error",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onApkDownloadProgress(BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showDownloadProgressNotification(totalBytes, soFarBytes, (soFarBytes * 100) / totalBytes);
                    }
                });
            }
        });
        ArrayList<CloudAppDetails> selfUpdateList = new ArrayList<>();
        selfUpdateList.add(cloudAppDetails);
        appDownloader.download(getApplicationContext().getFilesDir().getAbsolutePath(), selfUpdateList);
    }

    private void installUpdate() {

    }

    public class FetchInfoTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final JSONArray jsonArray) {
            // post fetch info result
            if (jsonArray == null) {
                return;
            }

            Type listType = new TypeToken<List<CloudSelfUpdateObject>>() {}.getType();
            Gson gson = new Gson();
            final List<CloudSelfUpdateObject> data = gson.fromJson(jsonArray.toString(), listType);
            if(mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        PreferencesHelper.getInstance(getApplicationContext())
                                .saveSelfUpdateInfo(jsonArray.toString());
                        if(data != null && !data.isEmpty()) {
                            showUpdateAvailableNotification(data.get(0));
                        }
                    }
                });
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String url = params[0];
            if (url == null) {
                return null;
            }

            JSONArray jsonArray = null;
            try
            {
                URL appsListURL = new URL (url);
                String responseText="";

                // Create Request to server and get response
                HttpURLConnection connection= (HttpURLConnection) appsListURL.openConnection();

                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream is = connection.getInputStream();
                responseText = IOUtils.toString(is);

                JSONObject json = new JSONObject(responseText);
                jsonArray = (JSONArray) json.get("data");

            } catch(Exception ex) {
                Log.e(TAG, ex.getMessage());
                return null;
            }

            return jsonArray;
        }
    }

    private void showUpdateAvailableNotification(CloudSelfUpdateObject cloudSelfUpdateObject) {
        Intent intent = new Intent(Consts.DOWNLOAD_UPDATE);
        intent.setClass(getApplicationContext(), UpdateCheckService.class);
        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 101, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new Notification.Builder(UpdateCheckService.this)
                .setContentTitle(getApplicationInfo().name)
                .setContentText("Update available")
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.wifi)
                .setWhen(System.currentTimeMillis());
        manager.notify(Consts.UPDATE_AVAILABLE, builder.build());
    }

    private void showDownloadProgressNotification(long total, long current, double progress) {
        Log.i(TAG, "Downloading self update");
        String remainingText = "Downloading";

        builder = new Notification.Builder(UpdateCheckService.this)
                .setContentTitle(getApplicationInfo().name)
                .setContentInfo(NumberFormat.getPercentInstance().format(progress))
                .setContentText(remainingText)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setWhen(System.currentTimeMillis())
                .setProgress(100, (int) (progress * 100), false);
        manager.notify(Consts.UPDATE_AVAILABLE, builder.build());
    }

    private void showInstallNotification() {
        Intent intent = new Intent(Consts.INSTALL_UPDATE);
        intent.setClass(getApplicationContext(), UpdateCheckService.class);
        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 101, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new Notification.Builder(UpdateCheckService.this)
                .setContentTitle(getApplicationInfo().name)
                .setContentText("Downloaded self update please install")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis())
                .setProgress(100, 100, false);

        manager.notify(Consts.UPDATE_AVAILABLE, builder.build());
    }
}
