package com.example.mridul.RetailerJunction.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.example.mridul.RetailerJunction.UTILS.AppInfoObj;
import com.example.mridul.RetailerJunction.UTILS.AppsList;
import com.example.mridul.RetailerJunction.UTILS.Constants;
import com.example.mridul.RetailerJunction.UTILS.DeviceInfoObj;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Mridul on 3/30/2016.
 */
public class httpServer extends NanoHTTPD {

    Context context;
    public AppsList appsList;

    public DeviceInfoObj devInfo;


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

        if (parms.get("getAppsList") != null) {

            appsList = new AppsList(context);

            return newFixedLengthResponse(appsList.getAppsListJson());
            //return new Response(Response.Status.OK, MIME_HTML, appsList.getAppsListJson());

        } else if (parms.get("submitCustData") != null) {
            // Save customer info for now. Ack to customer

            Gson gson = new Gson();
            String p = parms.get("data").toString();

            Type type = new TypeToken<DeviceInfoObj>() {}.getType();
            devInfo = gson.fromJson(p, type);

            // save to database. Later send to cloud
            Toast.makeText(context, "CustomerData: " + p, Toast.LENGTH_SHORT).show();


            // Ack to customer
            return newFixedLengthResponse("Customer Data submitted");
            //return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "Customer Data submitted");

        } else if (parms.get("getFile") != null) {
            String uri = session.getUri();
            File f = new File(".", uri);

            return serveFile(uri, session.getHeaders(), f, getMimeTypeForFile(uri));

        } else {
            String uri = session.getUri();
            File f = new File(".", uri);
            AssetManager mngr = context.getAssets();
            Response res = null;
            try {
                InputStream inputStream = mngr.open("customerkit.apk");
                res = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", inputStream, (int) inputStream.available());
                res.addHeader("Accept-Ranges", "bytes");
                res.addHeader("Content-Length", "" + inputStream.available());
                res.addHeader( "Content-Disposition", "attachment; filename=\"" + "customerkit.apk" + "\"");
                // res.addHeader("ETag", etag); //// TODO: 4/19/2016  
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

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
}

