package com.example.mridul.RetailerJunction.db;

import com.example.mridul.RetailerJunction.utils.DeviceInfoObject;

import java.util.List;


public class SubmitDataObject {

    /* installer data - who did */
    public PromoterInfoObject promoterInfo;

    /* Install records - what he did */
    public List<InstallRecordObject> installRecords;

    /* Device data - where he did */
    public DeviceInfoObject deviceDetails;

    /* Other details */
}
