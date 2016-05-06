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
      "id": 1,
      "name": "Redbus",
      "version": null,
      "size": "1mb",
      "downloadurl": "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk",
      "packagename": "com.redbus",
      "md5": "12345",
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

    public CloudAppsList(OfflineAppsFragment offlineAppsFragment) {
        fragment = offlineAppsFragment;
    }

    void ShowToast (String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public FetchAppsTask FetchCloudAppsList(Context c) {

        context = c;

        // fetch latest from cloud
        FetchAppsTask fetchAppsTask = new FetchAppsTask();
        fetchAppsTask.execute();

        return fetchAppsTask;
    }

    public List <CloudAppInfoObject> getList() {
        return null; //list;
    }

    public class FetchAppsTask extends AsyncTask<Void, Void, List<CloudAppInfoObject>> {

        List<CloudAppInfoObject> avilableAppsList;

        protected void onPostExecute(List<CloudAppInfoObject> list) {

            // tell UI that we were done
            fragment.onFetchCloudAppsListDone(list);
        }

        @Override
        protected List<CloudAppInfoObject> doInBackground(Void... voids) {

            try
            {
                URL appsListURL = new URL (Constants.APPSLIST_API_URL);
                String responseText="";

                // Create Request to server and get response
                HttpURLConnection connection= (HttpURLConnection) appsListURL.openConnection();

                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                class serverformat {
                    boolean success;
                    List<CloudAppInfoObject> data;
                    String message;
                }

                InputStream is = connection.getInputStream();
                responseText = IOUtils.toString(is);
                //responseText = "[{\"campaign_id\":10010,\"name\":\"Flipkart\",\"version\":\"1.1.2\",\"size\":\"984399\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Flipkart_4.5.apk\",\"packagename\":\"com.android.flipkart\",\"md5\":\"^&$#^&*%$(67432\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10013,\"name\":\"BookmyShow\",\"version\":\"783.2\",\"size\":\"7483725\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/BookMyShow.apk\",\"packagename\":\"com.bt.bms\",\"md5\":\"8754^&#94738954\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10014,\"name\":\"Gaana\",\"version\":\"82.2.2.2\",\"size\":\"84752\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Gaana_6.6.0.apk\",\"packagename\":\"com.times.gaana\",\"md5\":\"6546737234677980-\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10016,\"name\":\"HDFCBank\",\"version\":\"7.7.7.8\",\"size\":\"743782\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/HDFCBank.apk\",\"packagename\":\"com.snapwork.hdfc\",\"md5\":\"8493928492389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10019,\"name\":\"LavaTravel\",\"version\":\"8.3\",\"size\":\"1002\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/LavaTravel.apk\",\"packagename\":\"com.lava.travel\",\"md5\":\"7854735929893\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10020,\"name\":\"Myntra\",\"version\":\"3.33.3.3.3\",\"size\":\"8439789\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Myntra.apk\",\"packagename\":\"com.myntra.flipkart\",\"md5\":\"78943728\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10021,\"name\":\"OlaCabs\",\"version\":\"545\",\"size\":\"55443\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/OlaCabs.apk\",\"packagename\":\"ola.tfs.app\",\"md5\":\"894389829000934039090394009\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10022,\"name\":\"Piktures\",\"version\":\"72992.299993\",\"size\":\"88988999\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Piktures.apk\",\"packagename\":\"com.test.images.piktures\",\"md5\":\"743878275847389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10023,\"name\":\"inShorts\",\"version\":\"0.0.0.0.1\",\"size\":\"78787\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/inshorts.apk\",\"packagename\":\"inshorts.news\",\"md5\":\"898--9201312423209843209243-0\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10025,\"name\":\"AirLoyal-Ladooo\",\"version\":\"23\",\"size\":\"11233423\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/ladooo.apk\",\"packagename\":\"ladooo.airloyal.com\",\"md5\":\"89564347890574380758439\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"campaign_id\":10001,\"name\":\"Redbus\",\"version\":\"4.3.887.8\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/redBus_3.50.01.apk\",\"packagename\":\"com.redbus\",\"md5\":\"(*%&$*(&*473753\",\"state\":null,\"created_at\":\"2016-04-26 10:56:25\",\"updated_at\":\"2016-04-26 10:56:25\",\"deleted_at\":null},{\"campaign_id\":10004,\"name\":\"Clean Master\",\"version\":\"1.2.1\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/CleanMaster_5.11.8.apk\",\"packagename\":\"com.cleanmaster\",\"md5\":\"54785438977&#(483\",\"state\":null,\"created_at\":\"2016-05-03 05:51:03\",\"updated_at\":\"2016-05-03 05:51:03\",\"deleted_at\":null},{\"campaign_id\":10007,\"name\":\"Amazon\",\"version\":\"88.7.98.7.6..56.4..4\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Amazon.apk\",\"packagename\":\"in.amazon.mShop.android.shopping\",\"md5\":\"12345678900987654321\",\"state\":null,\"created_at\":\"2016-05-03 06:10:53\",\"updated_at\":\"2016-05-03 06:10:53\",\"deleted_at\":null},{\"campaign_id\":10017,\"name\":\"KnowStartup\",\"version\":\"6.0\",\"size\":\"54465\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/KnowStartup.apk\",\"packagename\":\"com.knowstartup.app\",\"md5\":\"8549390895435439869438\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null}]";

                //Hardcoaded test Data
                //responseText = "{\"success\":true,\"data\":[{\"id\":10010,\"name\":\"Flipkart\",\"version\":\"1.1.2\",\"size\":\"984399\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Flipkart_4.5.apk\",\"packagename\":\"com.android.flipkart\",\"md5\":\"^&$#^&*%$(67432\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10013,\"name\":\"BookmyShow\",\"version\":\"783.2\",\"size\":\"7483725\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/BookMyShow.apk\",\"packagename\":\"com.bt.bms\",\"md5\":\"8754^&#94738954\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10014,\"name\":\"Gaana\",\"version\":\"82.2.2.2\",\"size\":\"84752\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Gaana_6.6.0.apk\",\"packagename\":\"com.times.gaana\",\"md5\":\"6546737234677980-\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10016,\"name\":\"HDFCBank\",\"version\":\"7.7.7.8\",\"size\":\"743782\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/HDFCBank.apk\",\"packagename\":\"com.snapwork.hdfc\",\"md5\":\"8493928492389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10019,\"name\":\"LavaTravel\",\"version\":\"8.3\",\"size\":\"1002\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/LavaTravel.apk\",\"packagename\":\"com.lava.travel\",\"md5\":\"7854735929893\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10020,\"name\":\"Myntra\",\"version\":\"3.33.3.3.3\",\"size\":\"8439789\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Myntra.apk\",\"packagename\":\"com.myntra.flipkart\",\"md5\":\"78943728\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10021,\"name\":\"OlaCabs\",\"version\":\"545\",\"size\":\"55443\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/OlaCabs.apk\",\"packagename\":\"ola.tfs.app\",\"md5\":\"894389829000934039090394009\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10022,\"name\":\"Piktures\",\"version\":\"72992.299993\",\"size\":\"88988999\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Piktures.apk\",\"packagename\":\"com.test.images.piktures\",\"md5\":\"743878275847389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10023,\"name\":\"inShorts\",\"version\":\"0.0.0.0.1\",\"size\":\"78787\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/inshorts.apk\",\"packagename\":\"inshorts.news\",\"md5\":\"898--9201312423209843209243-0\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10025,\"name\":\"AirLoyal-Ladooo\",\"version\":\"23\",\"size\":\"11233423\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/ladooo.apk\",\"packagename\":\"ladooo.airloyal.com\",\"md5\":\"89564347890574380758439\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10001,\"name\":\"Redbus\",\"version\":\"4.3.887.8\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/redBus_3.50.01.apk\",\"packagename\":\"com.redbus\",\"md5\":\"(*%&$*(&*473753\",\"state\":null,\"created_at\":\"2016-04-26 10:56:25\",\"updated_at\":\"2016-04-26 10:56:25\",\"deleted_at\":null},{\"id\":10004,\"name\":\"Clean Master\",\"version\":\"1.2.1\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/CleanMaster_5.11.8.apk\",\"packagename\":\"com.cleanmaster\",\"md5\":\"54785438977&#(483\",\"state\":null,\"created_at\":\"2016-05-03 05:51:03\",\"updated_at\":\"2016-05-03 05:51:03\",\"deleted_at\":null},{\"id\":10007,\"name\":\"Amazon\",\"version\":\"88.7.98.7.6..56.4..4\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Amazon.apk\",\"packagename\":\"in.amazon.mShop.android.shopping\",\"md5\":\"12345678900987654321\",\"state\":null,\"created_at\":\"2016-05-03 06:10:53\",\"updated_at\":\"2016-05-03 06:10:53\",\"deleted_at\":null},{\"id\":10017,\"name\":\"KnowStartup\",\"version\":\"6.0\",\"size\":\"54465\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/KnowStartup.apk\",\"packagename\":\"com.knowstartup.app\",\"md5\":\"8549390895435439869438\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null}],\"message\":\"Apps retrieved successfully\"}";

                // 13 apps
                //responseText = "{\"success\":true,\"data\":[{\"id\":10016,\"name\":\"HDFCBank\",\"version\":\"7.7.7.8\",\"size\":\"743782\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/HDFCBank.apk\",\"packagename\":\"com.snapwork.hdfc\",\"md5\":\"8493928492389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10019,\"name\":\"LavaTravel\",\"version\":\"8.3\",\"size\":\"1002\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/LavaTravel.apk\",\"packagename\":\"com.lava.travel\",\"md5\":\"7854735929893\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10020,\"name\":\"Myntra\",\"version\":\"3.33.3.3.3\",\"size\":\"8439789\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Myntra.apk\",\"packagename\":\"com.myntra.flipkart\",\"md5\":\"78943728\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10021,\"name\":\"OlaCabs\",\"version\":\"545\",\"size\":\"55443\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/OlaCabs.apk\",\"packagename\":\"ola.tfs.app\",\"md5\":\"894389829000934039090394009\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10022,\"name\":\"Piktures\",\"version\":\"72992.299993\",\"size\":\"88988999\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Piktures.apk\",\"packagename\":\"com.test.images.piktures\",\"md5\":\"743878275847389\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10023,\"name\":\"inShorts\",\"version\":\"0.0.0.0.1\",\"size\":\"78787\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/inshorts.apk\",\"packagename\":\"inshorts.news\",\"md5\":\"898--9201312423209843209243-0\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10025,\"name\":\"AirLoyal-Ladooo\",\"version\":\"23\",\"size\":\"11233423\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/ladooo.apk\",\"packagename\":\"ladooo.airloyal.com\",\"md5\":\"89564347890574380758439\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null},{\"id\":10001,\"name\":\"Redbus\",\"version\":\"4.3.887.8\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/redBus_3.50.01.apk\",\"packagename\":\"com.redbus\",\"md5\":\"(*%&$*(&*473753\",\"state\":null,\"created_at\":\"2016-04-26 10:56:25\",\"updated_at\":\"2016-04-26 10:56:25\",\"deleted_at\":null},{\"id\":10004,\"name\":\"Clean Master\",\"version\":\"1.2.1\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/CleanMaster_5.11.8.apk\",\"packagename\":\"com.cleanmaster\",\"md5\":\"54785438977&#(483\",\"state\":null,\"created_at\":\"2016-05-03 05:51:03\",\"updated_at\":\"2016-05-03 05:51:03\",\"deleted_at\":null},{\"id\":10007,\"name\":\"Amazon\",\"version\":\"88.7.98.7.6..56.4..4\",\"size\":\"1000\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/Amazon.apk\",\"packagename\":\"in.amazon.mShop.android.shopping\",\"md5\":\"12345678900987654321\",\"state\":null,\"created_at\":\"2016-05-03 06:10:53\",\"updated_at\":\"2016-05-03 06:10:53\",\"deleted_at\":null},{\"id\":10017,\"name\":\"KnowStartup\",\"version\":\"6.0\",\"size\":\"54465\",\"downloadurl\":\"https:\\/\\/s3-ap-southeast-1.amazonaws.com\\/lavaretailapp\\/apps\\/KnowStartup.apk\",\"packagename\":\"com.knowstartup.app\",\"md5\":\"8549390895435439869438\",\"state\":null,\"created_at\":null,\"updated_at\":null,\"deleted_at\":null}],\"message\":\"Apps retrieved successfully\"}";


                //Type listType = new TypeToken<serverformat>() {}.getType();
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

            UpdateDatabase(avilableAppsList);

            return avilableAppsList;
        }
    }

    public interface onFetchCloudAppsListDone {
        public void onFetchCloudAppsListDone(List<CloudAppInfoObject> list);
    }

    void UpdateDatabase(List<CloudAppInfoObject> avilableAppsList) {

        // write to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

        UpdateRemovedCampagins(avilableAppsList, appEntryDao);

        UpdateOrAddCampagins(avilableAppsList, appEntryDao);

        db.close();
    }

    void UpdateRemovedCampagins(List<CloudAppInfoObject> cloudAppsList, CloudAppDetailsDao appEntryDao) {
        //delete from database

        List<CloudAppDetails> dbList = appEntryDao.loadAll();

        int i = 0;
        int j = 0;
        for (i=0; i<dbList.size(); i++) {
            // search DB entries in newly downloaded list
            for (j = 0; j < cloudAppsList.size(); j++) {

                if (dbList.get(i).getCampaign_id() == cloudAppsList.get(j).id) {
                    break;
                }
            }
            // if not found - deleted campaign
            if (j == cloudAppsList.size()) {
                // reached end of list without finding
                appEntryDao.deleteByKey(dbList.get(i).getCampaign_id());
            }
        }
    }

    void UpdateOrAddCampagins(List<CloudAppInfoObject> cloudAppsList, CloudAppDetailsDao appEntryDao) {


        for (int i=0; i<cloudAppsList.size(); i++) {

            CloudAppDetails dbItem = appEntryDao.load(cloudAppsList.get(i).id);
            CloudAppDetails c = new CloudAppDetails();

            /* if  found */
            if (dbItem != null) {

                // check MD5 to confirm any change in campaign
                // if there is a change then update record & remove downloaded flag
                if (dbItem.getMd5().equals(cloudAppsList.get(i).md5)) {
                    // MD5 matches - dont touch downloaded flag & apkts
                } else {
                    // MD5 different - make downloaded flag & ts false
                    c.setDownloaded(false);
                    c.setApkts(0L);

                    // remove file from apk folder
                    String APK_DIR = context.getApplicationContext().getFilesDir().getAbsolutePath();
                    String filename = APK_DIR + dbItem.getPackagename() + ".apk"; //use filename from db

                    File f = new File(filename);
                    try {
                        FileUtils.forceDelete(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                c.setDownloaded(false);
                c.setApkts(0L);
            }

            c.setCampaign_id(cloudAppsList.get(i).id);
            c.setName(cloudAppsList.get(i).name);
            c.setVersion(cloudAppsList.get(i).version);
            c.setSize(cloudAppsList.get(i).size);
            c.setDownloadurl(cloudAppsList.get(i).downloadurl);
            c.setPackagename(cloudAppsList.get(i).packagename);
            c.setMd5(cloudAppsList.get(i).md5);
            c.setState(cloudAppsList.get(i).state);

            c.setListts(System.currentTimeMillis());

            //download status & downloaded ts will be set by downloader
            // initialize to 0 for fresh entries.
            c.setDownloaded(false);
            c.setApkts(0L);

            appEntryDao.insertOrReplace(c);
        }
    }
}


/*
{"success":true,"data":[{"campaign_id":10010,"name":"Flipkart","version":"1.1.2","size":"984399","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Flipkart_4.5.apk","packagename":"com.android.flipkart","md5":"^&$#^&*%$(67432","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10013,"name":"BookmyShow","version":"783.2","size":"7483725","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/BookMyShow.apk","packagename":"com.bt.bms","md5":"8754^&#94738954","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10014,"name":"Gaana","version":"82.2.2.2","size":"84752","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Gaana_6.6.0.apk","packagename":"com.times.gaana","md5":"6546737234677980-","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10016,"name":"HDFCBank","version":"7.7.7.8","size":"743782","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/HDFCBank.apk","packagename":"com.snapwork.hdfc","md5":"8493928492389","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10019,"name":"LavaTravel","version":"8.3","size":"1002","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/LavaTravel.apk","packagename":"com.lava.travel","md5":"7854735929893","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10020,"name":"Myntra","version":"3.33.3.3.3","size":"8439789","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Myntra.apk","packagename":"com.myntra.flipkart","md5":"78943728","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10021,"name":"OlaCabs","version":"545","size":"55443","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/OlaCabs.apk","packagename":"ola.tfs.app","md5":"894389829000934039090394009","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10022,"name":"Piktures","version":"72992.299993","size":"88988999","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Piktures.apk","packagename":"com.test.images.piktures","md5":"743878275847389","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10023,"name":"inShorts","version":"0.0.0.0.1","size":"78787","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/inshorts.apk","packagename":"inshorts.news","md5":"898--9201312423209843209243-0","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10025,"name":"AirLoyal-Ladooo","version":"23","size":"11233423","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/ladooo.apk","packagename":"ladooo.airloyal.com","md5":"89564347890574380758439","state":null,"created_at":null,"updated_at":null,"deleted_at":null},{"campaign_id":10001,"name":"Redbus","version":"4.3.887.8","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/redBus_3.50.01.apk","packagename":"com.redbus","md5":"(*%&$*(&*473753","state":null,"created_at":"2016-04-26 10:56:25","updated_at":"2016-04-26 10:56:25","deleted_at":null},{"campaign_id":10004,"name":"Clean Master","version":"1.2.1","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/CleanMaster_5.11.8.apk","packagename":"com.cleanmaster","md5":"54785438977&#(483","state":null,"created_at":"2016-05-03 05:51:03","updated_at":"2016-05-03 05:51:03","deleted_at":null},{"campaign_id":10007,"name":"Amazon","version":"88.7.98.7.6..56.4..4","size":"1000","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/Amazon.apk","packagename":"in.amazon.mShop.android.shopping","md5":"12345678900987654321","state":null,"created_at":"2016-05-03 06:10:53","updated_at":"2016-05-03 06:10:53","deleted_at":null},{"campaign_id":10017,"name":"KnowStartup","version":"6.0","size":"54465","downloadurl":"https:\/\/s3-ap-southeast-1.amazonaws.com\/lavaretailapp\/apps\/KnowStartup.apk","packagename":"com.knowstartup.app","md5":"8549390895435439869438","state":null,"created_at":null,"updated_at":null,"deleted_at":null}],"message":"Apps retrieved successfully"}
*/