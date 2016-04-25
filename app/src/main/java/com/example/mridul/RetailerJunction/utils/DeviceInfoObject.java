package com.example.mridul.RetailerJunction.utils;

/**
 * Created by satish on 4/12/16.
 */

public class DeviceInfoObject {
    public String imei; // take care of multiple SIM case
    public String android_device_id;
    public String manufacturer;
    public String product;
    public String model;
    public String mac_id;
    public String resolution;
    public int dpi;

    public String os_version; // // TODO: 4/20/2016
    public String board_cpu; // decide board or CPU type
    public String device_id; // check what is it
    public String subscriber_id; //IMSI
    public String language; //locale
    public String timezone; //timezone
    public String operator; //MCC/MNC40445
    public String launcher_app; //which launcher is used
    public String phone_num; // try to get phone number
    public String android_api; // API level
    public int networkStatus; //2g/3g/4g/wifi
    public int rooted_yes_no; // rooted status
    public int internalAvail; //available internal mem
    public int internalTotal; //Total internal mem
    public int extAvail; //available internal mem
    public int extTotal; //Total external mem
    public int ramSize; //RAM size

    public String kitVersion;// kit app version
    // figure this out
    /*
    public long timestamp;
    public int kid; // dont know
    did // dont know
    public int size_system; //
    public String systemFs;
    */
}
