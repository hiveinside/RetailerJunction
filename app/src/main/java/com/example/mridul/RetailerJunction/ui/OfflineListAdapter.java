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
import android.widget.TextView;

import com.example.mridul.RetailerJunction.utils.AppInfoObject;
import com.example.mridul.helloworld.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Mridul on 4/29/2016.
 */

public class OfflineListAdapter extends ArrayAdapter<AppInfoObject> {
    private int list_item;
    private LayoutInflater inflater;
    private Context context;
    private List<String> mRetailerList;

    public OfflineListAdapter ( Context c, int resourceId, List<AppInfoObject> objects) {
        super( c, resourceId, objects );
        context = c;
        list_item = resourceId;
        inflater = LayoutInflater.from(c);
    }


    @Override
    public View getView (int position, View one_row, ViewGroup parent ) {
        one_row = (LinearLayout) inflater.inflate( list_item, null );
        AppInfoObject appInfo = getItem( position );

        ImageView icon = (ImageView) one_row.findViewById(R.id.appIcon);


        File imgFile = new File(RetailerApplication.getIconDir() + appInfo.packageName + ".png");
        if(imgFile.exists()) {
            icon.setImageURI(Uri.fromFile(imgFile));
        } else {
            icon.setImageResource(R.drawable.def);
        }

        TextView title = (TextView) one_row.findViewById(R.id.appTitle);
        title.setText(appInfo.appName);

        TextView detail = (TextView) one_row.findViewById(R.id.appDetail);

        Drawable image = context.getResources().getDrawable(getRatingDrawable(appInfo.rating));
        if (image != null) {
            image.setBounds(0, 0, image.getIntrinsicWidth()/3, image.getIntrinsicHeight()/3);
            detail.setCompoundDrawables(image, null, null, null);
        }
        detail.setText(" (" + bytesToHuman(appInfo.size) + ")");

        return one_row;
    }

    private int getRatingDrawable(float rating) {

        int id;
        if (rating > 4.5)
            id = R.drawable.stars_5;
        else if (rating > 4.0)
            id = R.drawable.stars_4_5;
        else if (rating > 3.5)
            id = R.drawable.stars_4;
        else if (rating > 3.0)
            id = R.drawable.stars_3_5;
        else if (rating > 2.5)
            id = R.drawable.stars_3;
        else if (rating > 2.0)
            id = R.drawable.stars_2_5;
        else if (rating > 1.5)
            id = R.drawable.stars_2;
        else if (rating > 1.0)
            id = R.drawable.stars_1_5;
        else
            id = R.drawable.stars_1;

        return id;
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.#").format(d);
    }

    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "???";
    }


}