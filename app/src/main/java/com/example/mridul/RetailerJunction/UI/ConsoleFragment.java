package com.example.mridul.RetailerJunction.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by satish on 4/12/16.
 */
public class ConsoleFragment extends Fragment {
    TableLayout tableLayout;

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
            tv.setTextSize(14);
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
}
