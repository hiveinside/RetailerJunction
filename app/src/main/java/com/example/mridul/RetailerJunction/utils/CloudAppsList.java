package com.example.mridul.RetailerJunction.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetailsDao;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.example.mridul.RetailerJunction.ui.OfflineAppsFragment;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Mridul on 4/29/2016.
 */

/*
{
  "success": true,
  "data": [
    {
      "campaign_id": 1,
      "name": "Redbus",
      "version": null,
      "size": "1mb",
      "downloadurl": "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk",
      "packagename": "com.redbus",
      "checksum": "12345",
      "state": null,
      "created_at": "2016-04-26 10:56:25",
      "updated_at": "2016-04-26 10:56:25",
      "deleted_at": null
    }
  ],
  "message": "Apps retrieved successfully"
}
 */
public class CloudAppsList {

    private Context context;
    OfflineAppsFragment fragment;
    private static List<CloudAppInfoObject> avilableAppsList;

    public CloudAppsList(OfflineAppsFragment offlineAppsFragment) {
        fragment = offlineAppsFragment;
    }

    public FetchAppsTask FetchCloudAppsList(Context c) {

        context = c;

        // fetch latest from cloud
        FetchAppsTask fetchAppsTask = new FetchAppsTask();
        fetchAppsTask.execute();

        return fetchAppsTask;
    }

    /*public static List<CloudAppInfoObject> getCurrentList() {
        return avilableAppsList;
    }*/
    public class FetchAppsTask extends AsyncTask<Void, Void, List<CloudAppDetails>> {

         protected void onPostExecute(List<CloudAppDetails> list) {

            // tell UI that we were done
            fragment.onFetchCloudAppsListDone(list);
        }

        @Override
        protected List<CloudAppDetails> doInBackground(Void... voids) {

            try
            {
                URL appsListURL = new URL (Constants.APPSLIST_API_URL +
                        "?token=" + PreferencesHelper.getInstance(context).getToken());
                String responseText="";

                // Create Request to server and get response
                HttpURLConnection connection= (HttpURLConnection) appsListURL.openConnection();

                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream is = connection.getInputStream();
                responseText = IOUtils.toString(is);

                Type listType = new TypeToken<List<CloudAppInfoObject>>() {}.getType();
                Gson gson = new Gson();

                JSONObject json = new JSONObject(responseText);
                JSONArray appslist = (JSONArray) json.get("data");
                List<CloudAppInfoObject> data = gson.fromJson(appslist.toString(), listType);

                avilableAppsList = data;


                //Type listType2 = new TypeToken<List<CloudAppInfoObject>>() {}.getType();
                //avilableAppsList = gson.fromJson(s.data, listType2);

            } catch(Exception ex) {
                Log.e("Fetch appslist Failed", ex.getMessage());
                return null;
            }

            return UpdateDatabase(avilableAppsList);
        }
    }

    public interface CloudAppsListCallback {
        public void onFetchCloudAppsListDone(List<CloudAppDetails> cloudList);
    }

    private List<CloudAppDetails> UpdateDatabase(List<CloudAppInfoObject> avilableAppsList) {

        // write to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

        UpdateRemovedCampagins(avilableAppsList, appEntryDao);

        UpdateOrAddCampagins(avilableAppsList, appEntryDao);

        // read updated list
        List<CloudAppDetails> dbList = appEntryDao.loadAll();

        db.close();

        return dbList;
    }

    private void UpdateRemovedCampagins(List<CloudAppInfoObject> cloudAppsList, CloudAppDetailsDao appEntryDao) {
        //delete from database

        List<CloudAppDetails> dbList = appEntryDao.loadAll();

        int i = 0;
        int j = 0;
        for (i=0; i<dbList.size(); i++) {
            // search DB entries in newly downloaded list
            for (j = 0; j < cloudAppsList.size(); j++) {

                if (dbList.get(i).getCampaignId() == cloudAppsList.get(j).campaign_id) {
                    break;
                }
            }
            // if not found - deleted campaign
            if (j == cloudAppsList.size()) {
                // reached end of list without finding
                appEntryDao.deleteByKey(dbList.get(i).getCampaignId());

                // update datastructures -- appsList
                //// TODO: 6/22/2016 decide if apk and icon should be deleted too
            }
        }
    }

    private void UpdateOrAddCampagins(List<CloudAppInfoObject> cloudAppsList, CloudAppDetailsDao appEntryDao) {


        for (int i=0; i<cloudAppsList.size(); i++) {

            CloudAppDetails dbItem = appEntryDao.load(cloudAppsList.get(i).campaign_id);
            //CloudAppDetails c = new CloudAppDetails();

            /* if  found */
            if (dbItem != null) {

                // check MD5 to confirm any change in campaign
                // if there is a change then update record & remove downloaded flag
                if (dbItem.getChecksum().equals(cloudAppsList.get(i).checksum)) {
                    // MD5 matches - dont touch downloaded flag & apkts

                } else {
                    // MD5 different - make downloaded flag & ts false
                    dbItem.setApkDownloaded(false);
                    dbItem.setIconDownloaded(false);
                    dbItem.setApkts(0L);

                    // remove file from apk folder
                    // may not be necessary to delete file

                    String APK_DIR = RetailerApplication.getApkDir();
                    String filename = APK_DIR + dbItem.getPackagename() + ".apk"; //use filename from db

                    File f = new File(filename);
                    try {
                        FileUtils.forceDelete(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                dbItem = new CloudAppDetails();

                dbItem.setCampaignId(cloudAppsList.get(i).campaign_id);
                dbItem.setApkDownloaded(false);
                dbItem.setIconDownloaded(false);
                dbItem.setApkts(0L);
            }


            dbItem.setName(cloudAppsList.get(i).name);
            dbItem.setVersion(cloudAppsList.get(i).version);
            dbItem.setSize(cloudAppsList.get(i).size);
            dbItem.setDownloadurl(cloudAppsList.get(i).downloadurl);
            dbItem.setPackagename(cloudAppsList.get(i).packagename);
            dbItem.setChecksum(cloudAppsList.get(i).checksum);
            dbItem.setState(cloudAppsList.get(i).state);
            dbItem.setMinsdk(cloudAppsList.get(i).minsdk);
            dbItem.setIconurl(cloudAppsList.get(i).iconurl);
            dbItem.setCategory(cloudAppsList.get(i).category);
            dbItem.setDesc(cloudAppsList.get(i).desc);
            dbItem.setRating(cloudAppsList.get(i).rating);

            dbItem.setListts(System.currentTimeMillis());

            //download status & downloaded ts will be set by downloader
            // initialize to 0 for fresh entries.
            // c.setDownloaded(false);
            // c.setApkts(0L);

            appEntryDao.insertOrReplace(dbItem);
        }
    }
}


/*
{"success":true,"data":[{"campaignId":10010,"name":"Flipkart","version":"1.1.2","size":"984399","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Flipkart_4.5.apk","packagename":"com.android.flipkart","checksum":"^&$#^&*%$(67432","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10013,"name":"BookmyShow","version":"783.2","size":"7483725","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/BookMyShow.apk","packagename":"com.bt.bms","checksum":"8754^&#94738954","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10014,"name":"Gaana","version":"82.2.2.2","size":"84752","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Gaana_6.6.0.apk","packagename":"com.times.gaana","checksum":"6546737234677980-","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10016,"name":"HDFCBank","version":"7.7.7.8","size":"743782","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/HDFCBank.apk","packagename":"com.snapwork.hdfc","checksum":"8493928492389","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10019,"name":"LavaTravel","version":"8.3","size":"1002","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/LavaTravel.apk","packagename":"com.lava.travel","checksum":"7854735929893","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10020,"name":"Myntra","version":"3.33.3.3.3","size":"8439789","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Myntra.apk","packagename":"com.myntra.flipkart","checksum":"78943728","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10021,"name":"OlaCabs","version":"545","size":"55443","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/OlaCabs.apk","packagename":"ola.tfs.app","checksum":"894389829000934039090394009","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10022,"name":"Piktures","version":"72992.299993","size":"88988999","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Piktures.apk","packagename":"com.test.images.piktures","checksum":"743878275847389","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10023,"name":"inShorts","version":"0.0.0.0.1","size":"78787","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/inshorts.apk","packagename":"inshorts.news","checksum":"898--9201312423209843209243-0","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10025,"name":"AirLoyal-Ladooo","version":"23","size":"11233423","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/ladooo.apk","packagename":"ladooo.airloyal.com","checksum":"89564347890574380758439","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaignId":10001,"name":"Redbus","version":"4.3.887.8","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/redBus_3.50.01.apk","packagename":"com.redbus","checksum":"(*%&$*(&*473753","state":null,"created_at":"2016-04-26 10:56:25","updated_at":"2016-04-26 10:56:25","deleted_at":null},{"campaignId":10004,"name":"Clean Master","version":"1.2.1","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/CleanMaster_5.11.8.apk","packagename":"com.cleanmaster","checksum":"54785438977&#(483","state":null,"created_at":"2016-05-03 05:51:03","updated_at":"2016-05-03 05:51:03","deleted_at":null},{"campaignId":10007,"name":"Amazon","version":"88.7.98.7.6..56.4..4","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Amazon.apk","packagename":"in.amazon.mShop.android.shopping","checksum":"12345678900987654321","state":null,"created_at":"2016-05-03 06:10:53","updated_at":"2016-05-03 06:10:53","deleted_at":null},{"campaignId":10017,"name":"KnowStartup","version":"6.0","size":"54465","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/KnowStartup.apk","packagename":"com.knowstartup.app","checksum":"8549390895435439869438","state":null,"created_at":null,"updated_at":null,"deleted_at":null}],"message":"Apps retrieved successfully"}
*/