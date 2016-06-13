package com.example.mridul.RetailerJunction.utils;

/**
 * Created by Mridul on 4/12/2016.
 */

/*
 *   ************************************************************
 *   ******************       WARNING            ****************
 *   ************************************************************
 *
 *   ENSURE THIS CLASS IS EXACTLY SAME IN RETAIL JUNCTION CLIENT
 *
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
    public int android_api; // API level
    public int network_status; //2g/3g/4g/wifi
    public boolean rooted; // rooted status
    public long internal_avail; //available internal mem
    public long internal_total; //Total internal mem
    public long external_avail; //available internal mem
    public long external_total; //Total external mem
    public long ram_total; //RAM size
    public long ram_avail; //RAM size
    public int kit_version_code;// kit app version
    public String kit_version_name;// kit app version

    // figure this out
    /*
    public long timestamp;
    public int kid; // dont know
    did // dont know
    public int size_system; //
    public String systemFs;
    */
}
