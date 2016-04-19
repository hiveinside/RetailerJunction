package com.example.mridul.RetailerJunction.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mridul.helloworld.R;

/**
 * Created by satish on 4/12/16.
 */
public class ConsoleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_console, container, false);

        return rootView;
    }
}
