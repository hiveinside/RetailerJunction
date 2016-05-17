package com.example.mridul.RetailerJunction.utils;

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
public class CloudAppInfoObject {
    public long campaign_id;
    public String name;
    public String version;
    public int size;
    public String downloadurl;
    public String packagename;
    public String checksum;
    public int state;
    public String created_at;
    public String updated_at;
    public String deleted_at;
}
