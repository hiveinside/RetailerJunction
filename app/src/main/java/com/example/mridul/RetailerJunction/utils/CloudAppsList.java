package com.example.mridul.RetailerJunction.utils;


import java.util.ArrayList;
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

    private List<CloudAppInfoObject> list = new ArrayList<CloudAppInfoObject>();;

    void FetchCloudAppsList() {
        // fetch latest from cloud

        // store in DB
/*
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.get, "-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
*/

        // Store current status of downloading list.
    }



    List <CloudAppInfoObject> getList() {

        list.add(getTmpObj1());
        list.add(getTmpObj2());
        list.add(getTmpObj3());

        return list;
    }


    CloudAppInfoObject getTmpObj1() {
        CloudAppInfoObject tmp = new CloudAppInfoObject();
        tmp.id = 1;
        tmp.name = "Amazon";
        tmp.version = "2.2.9";
        tmp.size = 54676554;
        tmp.downloadurl = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk";
        tmp.packagename = "com.amazon";
        tmp.md5 = "1234567890!@#$%^&*()";
        tmp.state = 1; // ????????????


        tmp.created_at = 1461938114006L;
        tmp.updated_at = 1461938114006L;
        tmp.deleted_at = 1461938114006L;

        return tmp;
    }

    CloudAppInfoObject getTmpObj2() {
        CloudAppInfoObject tmp = new CloudAppInfoObject();
        tmp.id = 2;
        tmp.name = "BookMyShow";
        tmp.version = "3.3.9";
        tmp.size = 6756463;
        tmp.downloadurl = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/BookMyShow.apk";
        tmp.packagename = "com.bt.bms";
        tmp.md5 = "1234567890!@#$%^&*()";
        tmp.state = 1; // ????????????


        tmp.created_at = 1461955114006L;
        tmp.updated_at = 1461955114006L;
        tmp.deleted_at = 1461955114006L;

        return tmp;
    }

    CloudAppInfoObject getTmpObj3() {
        CloudAppInfoObject tmp = new CloudAppInfoObject();
        tmp.id = 3;
        tmp.name = "CleanMaster";
        tmp.version = "4.4.9";
        tmp.size = 3456554;
        tmp.downloadurl = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/CleanMaster_5.11.8.apk";
        tmp.packagename = "com.clean.master.pkg";
        tmp.md5 = "1234567890!@#$%^&*()";
        tmp.state = 1; // ????????????


        tmp.created_at = 1461938322006L;
        tmp.updated_at = 1461938322006L;
        tmp.deleted_at = 1461938322006L;

        return tmp;
    }
}
