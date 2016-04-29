package com.example.mridul.RetailerJunction.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mridul.RetailerJunction.utils.AppInfoObj;
import com.example.mridul.helloworld.R;

import java.io.File;
import java.util.List;

/**
 * Created by Mridul on 4/29/2016.
 */

public class OfflineListAdapter extends ArrayAdapter<AppInfoObj> {
    private int list_item;
    private LayoutInflater inflater;

    public OfflineListAdapter ( Context c, int resourceId, List<AppInfoObj> objects) {
        super( c, resourceId, objects );
        list_item = resourceId;
        inflater = LayoutInflater.from(c);
    }


    @Override
    public View getView (int position, View one_row, ViewGroup parent ) {
        one_row = (LinearLayout) inflater.inflate( list_item, null );
        AppInfoObj appInfo = getItem( position );

        ImageView icon = (ImageView) one_row.findViewById(R.id.appIcon);


        File imgFile = new File("/sdcard/AppsShare/icons/" + appInfo.packageName + ".PNG");
        if(imgFile.exists()) {
            icon.setImageURI(Uri.fromFile(imgFile));
        }

        TextView title = (TextView) one_row.findViewById(R.id.appTitle);
        title.setText(appInfo.AppName);

        TextView detail = (TextView) one_row.findViewById(R.id.appDetail);
        detail.setText(appInfo.packageName + " | " + appInfo.size);

        return one_row;
    }
}