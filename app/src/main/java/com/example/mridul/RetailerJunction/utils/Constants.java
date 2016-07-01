package com.example.mridul.RetailerJunction.utils;

/**
 * Created by Mridul on 4/14/2016.
 */
public class Constants {
    public static final String DEFAULT_IP_ADDRESS = "http://192.168.43.1";
    public static final int HTTP_PORT=8888;
    public static final String DB_NAME = "rjdata-db";

    //public static final String APPSLIST_API_URL = "http://192.168.1.70:8080/retail/public/api/v1/apps";
    public static final String APPSLIST_API_URL = "http://retail.xapi.co.in/api/v1/apps";
    public static final String APPSLIST_LOGIN_URL = "http://retail.xapi.co.in/api/authenticate";
    public static final String APPSLIST_LOGOUT_URL = "http://retail.xapi.co.in/api/logout";
    public static final String UPLOADDATA_API_URL = "http://retail.xapi.co.in/api/events?token=";

    /*
     * URL&startdate=2016-05-01&enddate=2016-05-01&token=eyJ0eXAiOiJKV1QiLCJh
     */
    public static final String INCOME_REPORT_API_URL = "http://retail.xapi.co.in/api/report?";
}
