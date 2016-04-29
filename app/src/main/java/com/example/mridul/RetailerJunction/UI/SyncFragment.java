package com.example.mridul.RetailerJunction.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mridul.RetailerJunction.utils.AppInfoObj;
import com.example.mridul.RetailerJunction.utils.AppsList;
import com.example.mridul.helloworld.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 4/12/16.
 */
public class SyncFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);

        return rootView;
    }

    private final Runnable refreshing = new Runnable(){
        public void run(){
            try {
                // TODO : isRefreshing should be attached to your data request status
                if(swipeRefreshLayout.isRefreshing()){
                    // re run the verification after 1 second
                    handler.postDelayed(this, 1000);
                }else{
                    // stop the animation after the data is fully loaded
                    swipeRefreshLayout.setRefreshing(false);
                    // TODO : update your list with the new data



                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private List<AppInfoObj> getNewApps() {
        AppsList a = new AppsList(getActivity());
        List<AppInfoObj> newAppsList = a.getAppsList();

        swipeRefreshLayout.setRefreshing(false);

        TextView textView = (TextView) getActivity().findViewById(R.id.statusInfo);
        textView.setText("Last updated at: " + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));

        return newAppsList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            // find the layout
            swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);

            ListView listView = (ListView) getActivity().findViewById(R.id.offlinelist);
            listView.setAdapter(new OfflineListAdapter(getActivity().getApplicationContext(), R.layout.list_item, getNewApps()));

            // the refresh listner. this would be called when the layout is pulled down
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    // get the new data from you data source
                    // TODO : request data here
                    // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
                    handler.post(refreshing);

                    ListView listView = (ListView) getActivity().findViewById(R.id.offlinelist);
                    listView.setAdapter(new OfflineListAdapter(getActivity().getApplicationContext(), R.layout.list_item, getNewApps()));
                }
            });

            // sets the colors used in the refresh animation
            swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange, R.color.red);


            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable = true; //false;
                    if (view != null && view.getChildCount() > 0) {
                        // check if the first item of the list is visible
                        boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    swipeRefreshLayout.setEnabled(enable);
                }
            });
        }
    }
}
