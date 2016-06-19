package com.example.mridul.RetailerJunction.utils;

import java.util.Date;

/*
 *   ************************************************************
 *   ******************       WARNING            ****************
 *   ************************************************************
 *
 *   ENSURE THIS CLASS IS EXACTLY SAME IN RETAIL JUNCTION CLIENT
 *
 */

public class AppInfoObject {

    public long campaignId;
    public String appName;
    public String packageName;
    public String iconUrl;
    public String apkUrl;
    public String version;
    public String checksum;
    public int size; //bytes
    public int minsdk;

    /* Fields for client */
    public boolean downloadDone; //used by client
    public int installDone; //used by client
    public long installts; //used by client
}
