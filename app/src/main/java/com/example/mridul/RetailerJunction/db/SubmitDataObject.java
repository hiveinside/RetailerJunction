package com.example.mridul.RetailerJunction.db;

import com.example.mridul.RetailerJunction.utils.AppInfoObject;
import com.example.mridul.RetailerJunction.utils.DeviceInfoObject;

import java.util.List;


/*
 *   ************************************************************
 *   ******************       WARNING            ****************
 *   ************************************************************
 *
 *   ENSURE THIS CLASS IS EXACTLY SAME IN RETAIL JUNCTION CLIENT
 *
 */

public class SubmitDataObject {

    /* installer data - who did */
    public PromoterInfoObject promoterInfo;

    /* Install records - what he did */
    public List<AppInfoObject> installRecords;

    /* Device data - where he did */
    public DeviceInfoObject deviceDetails;

    /* Other details */
}
