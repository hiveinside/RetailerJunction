package com.example.mridul.RetailerJunction.http;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecords;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecordsDao;
import com.example.mridul.RetailerJunction.db.PromoterInfoObject;
import com.example.mridul.RetailerJunction.db.SubmitDataObject;
import com.example.mridul.RetailerJunction.ui.RetailerApplication;
import com.example.mridul.RetailerJunction.utils.AppsList;
import com.example.mridul.RetailerJunction.utils.Constants;
import com.example.mridul.helloworld.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Mridul on 3/30/2016.
 */
public class httpServer extends NanoHTTPD {

    Context context;
    public AppsList appsList;

    public httpServer(Context c) throws IOException
    {
        super(Constants.HTTP_PORT);
        context = c;
    }

    public httpServer(String hostname, Context c) {
        super(hostname, Constants.HTTP_PORT);
        context = c;
    }



    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> parms = session.getParms();

        Map<String, String> map = new HashMap<String, String>();
        Method method = session.getMethod();

        if ( Method.GET.equals(method) && parms.get("getAppsList") != null) {

            appsList = new AppsList(context);

            return newFixedLengthResponse(appsList.getAppsListJson());
            //return new Response(Response.Status.OK, MIME_HTML, appsList.getAppsListJson());

        } else if (Method.GET.equals(method) && parms.get("getIcon") != null) {
            String uri = session.getUri();
            File file = new File(RetailerApplication.getIconDir(), uri);

            Response res;
            try {
                res = newFixedFileResponse(file, getMimeTypeForFile(uri));
                res.addHeader("Content-Length", "" + file.length());
                // res.addHeader("ETag", etag); //// TODO: 5/5/2016  
            } catch (FileNotFoundException e) {
                res = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
            }

            return res;

        } else if (Method.GET.equals(method) && parms.get("getApk") != null) {
            String uri = session.getUri();
            File file = new File(RetailerApplication.getApkDir(), uri);

            Response res;
            try {
                res = newFixedFileResponse(file, getMimeTypeForFile(uri));
                res.addHeader("Content-Length", "" + file.length());
                // res.addHeader("ETag", etag); //// TODO: 5/5/2016
            } catch (FileNotFoundException e) {
                res = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
            }

            return res;

        } else if (Method.POST.equals(method)) {
            // Save customer info for now. Ack to customer
            SubmitDataObject datafromKit;
            String datafromKitJSON;

            try {
                session.parseBody(map);
            } catch (IOException ioe) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
            }

            Type type = new TypeToken<SubmitDataObject>() {}.getType();
            Gson gson = new Gson();
            datafromKitJSON = map.get("postData");
            datafromKit = gson.fromJson(datafromKitJSON, type);

            // save to database. Later send to cloud
            Log.e("CustomerData: ", datafromKitJSON);
            {
                getPromoterInfo(datafromKit.promoterInfo);

                addRecord(gson.toJson(datafromKit));
/*
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context.getApplicationContext());
                Log.e("dbObj: ", gson.toJson(datafromKit));
                databaseHelper.add(gson.toJson(datafromKit));
*/
            }

            // Ack to customer
            return newFixedLengthResponse("Customer Data submitted");
            //return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "Customer Data submitted");
        } else if (Method.GET.equals(method) && parms.get("KIT_APK_LINK") != null) {

            AssetManager mngr = context.getAssets();
            Response res = null;
            try {
                InputStream inputStream = mngr.open("customerkit.apk");
                res = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", inputStream, (int) inputStream.available());
                res.addHeader("Accept-Ranges", "bytes");
                res.addHeader("Content-Length", "" + inputStream.available());
                res.addHeader( "Content-Disposition", "attachment; filename=\"" + "customerkit.apk" + "\"");
                // res.addHeader("ETag", etag); //// TODO: 4/19/2016 figure how to set ETAG.. and is it really needed?

            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        } else if (Method.GET.equals(method) && parms.get("SHARE_APK_LINK") != null) {

            AssetManager mngr = context.getAssets();
            Response res = null;
            try {
                InputStream inputStream = mngr.open("shareapp.apk");
                res = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", inputStream, (int) inputStream.available());
                res.addHeader("Accept-Ranges", "bytes");
                res.addHeader("Content-Length", "" + inputStream.available());
                res.addHeader( "Content-Disposition", "attachment; filename=\"" + "shareapp.apk" + "\"");
                // res.addHeader("ETag", etag); //// TODO: 4/19/2016 figure how to set ETAG.. and is it really needed?

            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        //return super.serve(session);
        // any other request
        // server html page
        AssetManager mngr = context.getAssets();
        Response res = null;
        try {
            InputStream inputStream = mngr.open("index.html");
            res = newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, inputStream, (int) inputStream.available());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;


/*
        else {

            AssetManager mngr = context.getAssets();
            try {
                InputStream is = mngr.open("customerkit.apk");

                Response response = new Response(Response.Status.OK, "application/octet-stream", is);
                response.addHeader( "Content-Length", "" + is.available());
                response.addHeader( "Content-Disposition", "attachment; filename=\"" + "customerkit.apk" + "\"");
                //response.addHeader("ETag", etag); // TODO:

                response.addHeader( "Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
                return response;
                //return super.serve(uri, method, header, parms, files);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/
        //// TODO: 4/19/2016 do we need this?
        //return super.serve(session);
    }

    private void getPromoterInfo(PromoterInfoObject promoterInfo) {
        // // TODO: 5/17/2016 get this once login functionality is done
        promoterInfo.promoterId = new String("9243090116");

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        // TODO: 5/17/2016 get both/all IMEIs
        promoterInfo.imei = telephonyManager.getDeviceId();
        promoterInfo.android_id = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        promoterInfo.model = Build.BRAND + "," + Build.PRODUCT + "," + Build.MODEL;

        // get your own version
        promoterInfo.shareAppVersionCode = BuildConfig.VERSION_CODE;
        promoterInfo.shareAppVersionName = BuildConfig.VERSION_NAME;
    }

    Response serveFile(String uri, Map<String, String> header, File file, String mime) {
        Response res;
        try {
            // Calculate etag
            String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            String ifRange = header.get("if-range");
            boolean headerIfRangeMissingOrMatching = (ifRange == null || etag.equals(ifRange));

            String ifNoneMatch = header.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*".equals(ifNoneMatch) || ifNoneMatch.equals(etag));

            // Change return code and add Content-Range header when skipping is
            // requested
            long fileLen = file.length();

            if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    FileInputStream fis = new FileInputStream(file);
                    fis.skip(startFrom);

                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, fis, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {

                if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes */" + fileLen);
                    res.addHeader("ETag", etag);
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    // supply the file
                    res = newFixedFileResponse(file, mime);
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }

        return res;
    }

    private Response newFixedFileResponse(File file, String mime) throws FileNotFoundException {
        Response res;
        res = newFixedLengthResponse(Response.Status.OK, mime, new FileInputStream(file), (int) file.length());
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private void addRecord(String json_data) {
        // write to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        InstallRecordsDao installRecordsDao = daoSession.getInstallRecordsDao();

        InstallRecords installRecords = new InstallRecords();

        installRecords.setJson_data(json_data);
        installRecords.setTimestamp(System.currentTimeMillis());
        installRecords.setIsUploaded(0); // Not uploaded yet

        installRecordsDao.insert(installRecords);
    }
}

