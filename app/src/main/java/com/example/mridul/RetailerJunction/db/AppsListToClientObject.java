package com.example.mridul.RetailerJunction.db;

import com.example.mridul.RetailerJunction.utils.AppInfoObject;
import com.example.mridul.RetailerJunction.utils.AppsList;

import java.util.List;

/**
 * Created by Mridul on 6/21/2016.
 */

/*
 *   ************************************************************
 *   ******************       WARNING            ****************
 *   ************************************************************
 *
 *   ENSURE THIS CLASS IS EXACTLY SAME IN RETAIL JUNCTION CLIENT
 *
 */
public class AppsListToClientObject {

    /* List of app objects */
    public List<AppInfoObject> appsList;

    /* promoter ID reference. Not used at this point */
    public String promoterId;

    /* promoter ts for reference. customer time may be old. */
    public long timestamp;

}
