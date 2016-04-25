package com.example.mridul.RetailerJunction.db;

import com.example.mridul.RetailerJunction.utils.DeviceInfoObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class dbDataObject {

    /* installer data - who did */
    public PromoterInfoObject promoterInfo;

    /* Install records - what he did */
    public List<installRecordObject> installRecords;

    /* Device data - where he did */
    public DeviceInfoObject deviceDetails;

    /* Other details */
}
