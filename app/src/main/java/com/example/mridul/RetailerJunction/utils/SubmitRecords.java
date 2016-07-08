package com.example.mridul.RetailerJunction.utils;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecords;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecordsDao;
import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Mridul on 4/17/2016.
 */
public class SubmitRecords extends AsyncTask<Void, Void, Boolean> {

    protected void onPostExecute(Boolean result) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        InstallRecordsDao installRecordsDao = daoSession.getInstallRecordsDao();


        List<InstallRecords> installRecordsList;
        installRecordsList = null;

        if (installRecordsDao != null) {
            installRecordsList = installRecordsDao.loadAll();
        }

        String auth_token = PreferencesHelper.getInstance(RetailerApplication.getRJContext()).getToken();
        String urlString = Constants.UPLOADDATA_API_URL + auth_token;
        for (int i=0; i<installRecordsList.size(); i++) {

            if (installRecordsList.get(i).getIsUploaded() == 0) {

                try {
                    if (auth_token == null){
                        return null;
                    }
                    URL submitURL = new URL(urlString);
                    String postData = "values=" + installRecordsList.get(i).getJson_data();
                    Log.e("SubmitRecordsTask", installRecordsList.get(i).getJson_data());

                    HttpURLConnection conn = (HttpURLConnection) submitURL.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Content-Length", String.valueOf(postData.length()));
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Connection", "close");

                    byte[] postDataBytes = postData.getBytes("UTF-8");
                    conn.getOutputStream().write(postDataBytes);

                    int responseCode = conn.getResponseCode();

                    String response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    Log.e("submitCustData", response);
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        Log.e("Submit data Failed: ", response.toString());
                        continue;
                    }

                    // update uploaded flag
                    InstallRecords dbItem = installRecordsDao.loadByRowId(i+1); // row id starts from 1
                    dbItem.setIsUploaded(1); // 1 - is yes
                    installRecordsDao.insertOrReplace(installRecordsList.get(i));

                } catch (Exception e) {
                    Log.e("Submit data Failed: ", e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
}
