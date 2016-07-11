package com.example.mridul.RetailerJunction.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mridul on 4/17/2016.
 */
public class GetIncomeReport extends AsyncTask<Void, Void, List<IncomeReportObject>> {

    Context mContext;

    public GetIncomeReport(Context context) {
        mContext = context;
    }

    protected void onPostExecute(List<IncomeReportObject> incomeData) {
        // store in db

    }

    @Override
    protected List<IncomeReportObject> doInBackground(Void... voids) {

        List<IncomeReportObject> incomeData;

        try
        {
            /*
            * URL&startdate=2016-05-01&enddate=2016-05-01&token=eyJ0eXAiOiJKV1QiLCJh
            */
            Calendar todate = Calendar.getInstance();
            Calendar fromDate = Calendar.getInstance();
            fromDate.add(Calendar.DATE, -10);

            String auth_token = PreferencesHelper.getInstance(mContext).getToken();

            if (auth_token == null){
                return null;
            }

            String incomeAPILink = Constants.INCOME_REPORT_API_URL + "&" +
                                "startdate=" + new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getTime()) + "&" +
                                "enddate=" + new SimpleDateFormat("yyyy-MM-dd").format(todate.getTime()) + "&" +
                                "token=" + auth_token;
            URL incomeApiURL = new URL (incomeAPILink);
            String responseText="";

            // Create Request to server and get response
            HttpURLConnection connection= (HttpURLConnection) incomeApiURL.openConnection();

            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream is = connection.getInputStream();
            responseText = IOUtils.toString(is);
            //Log.e("income data", responseText);

            Type listType = new TypeToken<List<IncomeReportObject>>() {}.getType();
            Gson gson = new Gson();

            JSONObject json = new JSONObject(responseText);
            JSONArray incomeRecords = (JSONArray) json.get("data");
            incomeData = gson.fromJson(incomeRecords.toString(), listType);

            // write to sharedprefs
            PreferencesHelper.getInstance(mContext).saveWeekIncome(incomeRecords.toString());

        } catch (org.json.JSONException ex) {
            // ignore
            PreferencesHelper.getInstance(mContext).saveWeekIncome("");
            return null;

        } catch(Exception ex) {
            Log.e("Fetch income Failed", ex.getMessage());
            return null;
        }

        return incomeData;
    }
}
