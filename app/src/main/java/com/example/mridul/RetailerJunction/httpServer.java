package com.example.mridul.RetailerJunction;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mridul on 3/30/2016.
 */
public class httpServer extends NanoHTTPD {

    Context context;
    public AppsList appsList;

    public DeviceInfoObj devInfo;


    public httpServer(Context c) throws IOException
    {
        super(8080, new File("."));
        //super(8080, new File("."));
        context = c;
    }



    public Response serve( String uri, String method, Properties header, Properties parms, Properties files )  {

       /* if ( parms.get("getIcon") != null ) {

            return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "http://192.168.34.1:8080/sdcard/AppsShare/amazon.bmp");

        } else*/
        Log.d("MikeTesting","Entering loop");

        if (parms.get("getAppsList") != null) {

            appsList = new AppsList(context);

            return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, appsList.getAppsListJson());

        } else if (parms.get("submitCustData") != null) {
            // Save customer info for now. Ack to customer

            Gson gson = new Gson();
            String p = parms.get("data").toString();

            Type type = new TypeToken<DeviceInfoObj>() {}.getType();
            devInfo = gson.fromJson(p, type);

            // Ack to customer
            return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "Customer Data submitted");
        }
        else {
            return super.serve(uri, method, header, parms, files);
        }
    }

    private String getAppsListTest() {

        List<AppInfoObj> appsList = new ArrayList<AppInfoObj>();


        AppInfoObj testApp1 = new AppInfoObj();
        testApp1.AppName = "Amazon";
        testApp1.packageName = "com.public.amazon";
        testApp1.description = "Amazon e-commerce app";
        testApp1.iconUrl = "http://192.168.43.1:8080/sdcard/AppsShare/amazon.bmp";
        testApp1.apkUrl = "http://192.168.34.1:8080/amazon.apk";

        testApp1.version = "1.2.3.4.5.6";
        testApp1.md5 = "HJFD&*WEJKJFKS(*@$$@HJHDFJ674367";
        testApp1.size = 365584;
        testApp1.date = new Date(2016, 4, 01);
        testApp1.installPrice = (float) 2.0;
        testApp1.activatePrice = (float) 3;

        appsList.add(testApp1);

        AppInfoObj testApp2 = new AppInfoObj();
        testApp2.AppName = "Amazon";
        testApp2.description = "Amazon e-commerce app";
        testApp2.packageName = "com.public.amazon";
        testApp2.iconUrl = "http://192.168.43.1:8080/sdcard/AppsShare/amazon.bmp";
        testApp2.apkUrl = "http://192.168.34.1:8080/amazon.apk";


        testApp2.version = "1.2.3.4.5.6";
        testApp2.md5 = "HJFD&*WEJKJFKS(*@$$@HJHDFJ674367";
        testApp2.size = 365584;
        testApp2.date = new Date(2016, 4, 01);
        testApp2.installPrice = (float) 2.0;
        testApp2.activatePrice = (float) 3;

        appsList.add(testApp2);
        appsList.add(testApp2);
        appsList.add(testApp2);




        //convert to json
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.create();

        //Log.e("MikeTesting GSON---->>", gson.toJson(appsList));

        return gson.toJson(appsList);
    }
}

