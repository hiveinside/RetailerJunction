package com.example.mridul.RetailerJunction.ui;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetailsDao;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoMaster;
import com.example.mridul.RetailerJunction.daogenerator.model.DaoSession;
import com.example.mridul.RetailerJunction.utils.AppDownloader;
import com.example.mridul.RetailerJunction.utils.AppInfoObject;
import com.example.mridul.RetailerJunction.utils.AppsList;
import com.example.mridul.RetailerJunction.utils.CloudAppsList;
import com.example.mridul.RetailerJunction.utils.Constants;
import com.example.mridul.RetailerJunction.utils.SubmitRecords;
import com.example.mridul.helloworld.R;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class OfflineAppsFragment extends Fragment implements CloudAppsList.CloudAppsListCallback, AppDownloader.AppDownloadCallback {

    private static List<CloudAppDetails> dbCloudAppsList;

    CloudAppsList.FetchAppsTask appsListTask;
    OfflineListAdapter mListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    static long lastSyncTime;

    static final int APKCOMPLETED = 1;
    static final int PROGRESS = 2;
    static final int ERROR = 3;
    static final int NOTHINGTODOWNLOAD = 4;
    static final int OFFLINE = 5;

    int soFarBytes;
    int totalBytes;

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

    void ShowToast (String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);

        // read database - load last cloudAppDetail from DB
        loadAppsListFromDB();
        if (dbCloudAppsList.size() > 0) {
            lastSyncTime = dbCloudAppsList.get(0).getListts();
        }

        return rootView;
    }

    private void loadAppsListFromDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RetailerApplication.getRJContext(), Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        CloudAppDetailsDao appEntryDao = daoSession.getCloudAppDetailsDao();

        dbCloudAppsList = appEntryDao.loadAll();
    }

    private List<AppInfoObject> getExistingApps() {

        return AppsList.getAppsList();
    }

    private List<AppInfoObject> getNewApps() {

        List<AppInfoObject> newAppsList = AppsList.getAppsList();

        CloudAppsList cloudAppsList = new CloudAppsList(this);
        cloudAppsList.FetchCloudAppsList(this.getActivity());


        // also submit any pending data
        SubmitRecords s = new SubmitRecords();
        s.execute();

        return newAppsList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            // find the layout
            swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);

            ListView listView = (ListView) getActivity().findViewById(R.id.offlinelist);

            // popualate existing list.
            listView.setAdapter(new OfflineListAdapter(getActivity().getApplicationContext(), R.layout.list_item, getExistingApps()));
            
            // // TODO: 5/10/2016 figure this. Offline listing needs last available 
            UpdateUI(null, OFFLINE);

            // the refresh listner. this would be called when the layout is pulled down
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    // get the new data from you data source
                    // TODO : request data here
                    // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation

                    handler.post(refreshing);

                    ListView listView = (ListView) getActivity().findViewById(R.id.offlinelist);
                    mListAdapter = new OfflineListAdapter(getActivity().getApplicationContext(), R.layout.list_item, getNewApps());
                    listView.setAdapter(mListAdapter);
                }
            });

            // sets the colors used in the refresh animation
            swipeRefreshLayout.setColorSchemeResources(R.color.darkblue, R.color.darkgreen, R.color.darkorange, R.color.darkred);


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

    @Override
    public void onFetchCloudAppsListDone(List<CloudAppDetails> newList) {

        if (newList == null) {
            ShowToast("Error syncing appslist");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        //ShowToast("" + newList.size() + " apps available");


        // update dblist with this new one
        dbCloudAppsList = newList;

        //parse appslist again here.. apps may have been deleted
        // // TODO: 5/7/2016 just update what is needed - add, update, delete
        AppsList a = new AppsList(getActivity());



        /*
         * Send request to downloader
         */
        List<CloudAppDetails> downloadList = new ArrayList<CloudAppDetails>();;

        List<AppInfoObject> appsList = AppsList.getAppsList();

        int i = 0;
        int j = 0;
        for( i=0; i<dbCloudAppsList.size(); i++) {
            for( j=0; j<appsList.size(); j++) {
                if ( appsList.get(j).campaignId == dbCloudAppsList.get(i).getCampaignId()) {
                    // found in appslist.. already downloaded
                    break;
                }
            }

            if ( j == appsList.size()) {
                // not found.. download now.
                downloadList.add(dbCloudAppsList.get(i));
            }
        }

        // download if there is anything to download.
        if (downloadList.size() > 0) {
            AppDownloader ad = new AppDownloader(this);
            ad.download(getActivity().getApplicationContext().getFilesDir().getAbsolutePath(), downloadList);
        } else {
            UpdateUI(null, NOTHINGTODOWNLOAD);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // stop threads - applistdownloader, appdownloader
        // otherwise it will crash

        if( appsListTask != null)
            appsListTask.cancel(true);

        // do this if downloads are active. Otherwise it will show error unnecessarily
        FileDownloader.getImpl().pauseAll();

        UpdateUI(null, ERROR);
    }

    @Override
    public void onApkDownloadCompleted(BaseDownloadTask task) {

        // Refresh appsList datastructure -  this will reparse the apks
        // // TODO: 5/7/2016 just update what is needed - add, update, delete
        AppsList a = new AppsList(getActivity());
        String filename = ((CloudAppDetails) task.getTag()).getPackagename() + ".apk";
        //a.addToList(campaignId, filename);

        // Refresh UI
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }

        //update UI
        UpdateUI(task, APKCOMPLETED);
    }

    @Override
    public void onApkDownloadError(BaseDownloadTask task) {
        UpdateUI(task, ERROR);
    }

    @Override
    public void onApkDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        this.soFarBytes = soFarBytes;
        this.totalBytes = totalBytes;
        UpdateUI(task, PROGRESS);
    }

    void UpdateUI(BaseDownloadTask task, int state) {
        // update UI
        TextView textLeft = (TextView) getActivity().findViewById(R.id.infoLeft);
        TextView textRight = (TextView) getActivity().findViewById(R.id.infoRight);

        // needed in all cases
        textRight.setText("Apps (" + AppsList.getAppsList().size() + "/" + dbCloudAppsList.size() + ")");

        switch (state){
            case OFFLINE:
                String when;
                if (lastSyncTime > 0) {
                    when = DateFormat.getDateTimeInstance().format(lastSyncTime);
                } else {
                    when = new String("Never");
                }
                textLeft.setText("Updated: " + when);
                break;

            case NOTHINGTODOWNLOAD:
                ShowToast("Sync completed");

                lastSyncTime = System.currentTimeMillis();
                swipeRefreshLayout.setRefreshing(false);
                textLeft.setText("Updated: " + DateFormat.getDateTimeInstance().format(lastSyncTime));
                break;

            case APKCOMPLETED:
                // all downloads complete?
                if (AppsList.getAppsList().size() == dbCloudAppsList.size()) {
                    ShowToast("Sync completed");

                    swipeRefreshLayout.setRefreshing(false);
                    lastSyncTime = System.currentTimeMillis();
                    textLeft.setText("Updated: " + DateFormat.getDateTimeInstance().format(lastSyncTime));
                } else{
                    textLeft.setText("");
                }
                break;

            case PROGRESS:
                textLeft.setText("Downloading: " + ((CloudAppDetails)task.getTag()).getName() + "..." + Integer.toString((soFarBytes*100/totalBytes)+1) + "%");
                break;

            case ERROR:
                ShowToast("Error downloading apps");

                swipeRefreshLayout.setRefreshing(false);
                textLeft.setText("Updated: " + DateFormat.getDateTimeInstance().format(lastSyncTime));
                break;

            default: // bad
                break;
        }
    }
}
