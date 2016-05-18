package com.example.mridul.RetailerJunction.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecords;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecordsDao;
import com.example.mridul.RetailerJunction.db.SubmitDataObject;
import com.example.mridul.RetailerJunction.utils.Constants;
import com.example.mridul.helloworld.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 4/12/16.
 */
public class ConsoleFragment extends Fragment {
    TableLayout tableLayout;
    Button button;
    Context appcontext;
    Context activitycontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_console, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableLayout = (TableLayout) getActivity().findViewById(R.id.recordTable);
        button = (Button) getActivity().findViewById(R.id.submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // submit async
                SubmitRecordsTask s = new SubmitRecordsTask();
                s.execute();
            }
        });

        addTableHeaders();
    }

    @Override
    public void onResume() {
        super.onResume();

        addTableRows();
    }

    private void addTableHeaders() {
        TableRow rowHeader = new TableRow(getActivity());
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"IMEI", "MODEL", "UPLOADED"};
        for (String c : headerText) {
            TextView tv = new TextView(getActivity());
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(c);
            rowHeader.addView(tv);
        }
        tableLayout.addView(rowHeader);
    }

    private void addTableRows() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        InstallRecordsDao installRecordsDao = daoSession.getInstallRecordsDao();


        List<InstallRecords> installRecordsList;
        installRecordsList = null;

        if (installRecordsDao != null) {
            installRecordsList = installRecordsDao.loadAll();
        }

        for (int i=0; i<installRecordsList.size(); i++) {

            String json_data = installRecordsList.get(i).getJson_data();

            SubmitDataObject record;

            Type type = new TypeToken<SubmitDataObject>() {}.getType();
            Gson gson = new Gson();
            record = gson.fromJson(json_data, type);

            String imei = record.deviceDetails.imei;
            String model = record.deviceDetails.model;
            String uploaded = (installRecordsList.get(i).getIsUploaded()==0)?"No":"Yes";


            String[] colText = {imei, model, uploaded};

            TableRow row = new TableRow(getActivity());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (String text:colText) {
                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(12);
                tv.setPadding(5, 5, 5, 5);
                tv.setText(text);
                row.addView(tv);
            }
            tableLayout.addView(row);
        }
        db.close();
    }

    private class SubmitRecordsTask extends AsyncTask<Void, Void, Boolean> {

        protected void onPostExecute(Boolean result) {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
            SQLiteDatabase db = helper.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            InstallRecordsDao installRecordsDao = daoSession.getInstallRecordsDao();


            List<InstallRecords> installRecordsList;
            installRecordsList = null;

            if (installRecordsDao != null) {
                installRecordsList = installRecordsDao.loadAll();
            }

            for (int i=0; i<installRecordsList.size(); i++) {

                if (installRecordsList.get(i).getIsUploaded() == 0) {

                    try {
                        String URL = Constants.UPLOADDATA_API_URL + Constants.AUTH_TOKEN;
                        HttpPost httpPost = new HttpPost(URL);
                        HttpClient client = new DefaultHttpClient();

                        Log.e("SubmitRecordsTask", installRecordsList.get(i).getJson_data());
                        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                        postParameters.add(new BasicNameValuePair("values", installRecordsList.get(i).getJson_data()));

                        //httpPost.setEntity(new StringEntity(obj.toString()));
                        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));


                        HttpResponse response = client.execute(httpPost);
                        if (response.getStatusLine().getStatusCode() != 200) {
                            Log.e("Submit data Failed: ", response.toString());
                            return false;
                        }

                        // update uploaded flag
                        InstallRecords dbItem = installRecordsDao.loadByRowId(i+1); // row id starts from 1
                        dbItem.setIsUploaded(1); // 1 - is yes
                        installRecordsDao.insertOrReplace(installRecordsList.get(i));

                    } catch (Exception e) {
                        Log.e("Submit data Failed: ", e.getMessage());
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
