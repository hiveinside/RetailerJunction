package com.example.mridul.RetailerJunction.utils;

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
      "version": "4.3.887.8",
      "size": "1000",
      "downloadurl": "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/apps/redBus_3.50.01.apk",
      "packagename": "com.redbus",
      "checksum": "Wjm3XZgM7L",
      "state": null,
      "campaign_id": 1,
      "minsdk": 19,
      "iconurl": "https://s3-ap-southeast-1.amazonaws.com/lavaretailapp/icons/in.redbus.android.PNG",
      "category": "Travel & Local",
      "desc": "Bus ticket booking made easy",
      "rating": 4
    }
  ],
  "message": "Apps retrieved successfully"
}
*/
public class CloudAppInfoObject {
    public long campaign_id;
    public String name;
    public String version;
    public int size;
    public String downloadurl;
    public String packagename;
    public String checksum;
    public int state;
    public int minsdk;
    public String iconurl;
    public String category;
    public String desc;
    public float rating;
}
