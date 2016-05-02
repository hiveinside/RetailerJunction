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
public class GetAppsList {

    List<CloudAppInfoObject> cloudAppInfoList;

    List <CloudAppInfoObject> fetchAppsList() {

        List<CloudAppInfoObject> c = new ArrayList<CloudAppInfoObject>();;


        c.add(getTmpObj1());
        c.add(getTmpObj2());
        c.add(getTmpObj3());

        return c;
    }


    CloudAppInfoObject getTmpObj1() {
        CloudAppInfoObject tmp = new CloudAppInfoObject();
        tmp.id = 12345;
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
        tmp.id = 12345;
        tmp.name = "Flikart";
        tmp.version = "3.3.9";
        tmp.size = 54334532;
        tmp.downloadurl = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk";
        tmp.packagename = "com.flipkart";
        tmp.md5 = "1234567890!@#$%^&*()";
        tmp.state = 1; // ????????????


        tmp.created_at = 1461938114006L;
        tmp.updated_at = 1461938114006L;
        tmp.deleted_at = 1461938114006L;

        return tmp;
    }

    CloudAppInfoObject getTmpObj3() {
        CloudAppInfoObject tmp = new CloudAppInfoObject();
        tmp.id = 12345;
        tmp.name = "Redbus";
        tmp.version = "1.1.9";
        tmp.size = 3456554;
        tmp.downloadurl = "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk";
        tmp.packagename = "com.redbus";
        tmp.md5 = "1234567890!@#$%^&*()";
        tmp.state = 1; // ????????????


        tmp.created_at = 1461938114006L;
        tmp.updated_at = 1461938114006L;
        tmp.deleted_at = 1461938114006L;

        return tmp;
    }
}
