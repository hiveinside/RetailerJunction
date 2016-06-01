package com.example.mridul.RetailerJunction.utils;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecords;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecordsDao;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
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

        for (int i=0; i<installRecordsList.size(); i++) {

            if (installRecordsList.get(i).getIsUploaded() == 0) {

                try {
                    String URL = Constants.UPLOADDATA_API_URL + Constants.AUTH_TOKEN;
                    HttpPost httpPost = new HttpPost(URL);
                    HttpClient client = new DefaultHttpClient();

                    Log.e("SubmitRecordsTask", installRecordsList.get(i).getJson_data());
                    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                    postParameters.add(new BasicNameValuePair("values", installRecordsList.get(i).getJson_data()));

                    //httpPost.setEntity(new StringEntity(obj.toString()));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));


                    HttpResponse response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() != 200) {
                        Log.e("Submit data Failed: ", response.toString());
                        //return false;
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
