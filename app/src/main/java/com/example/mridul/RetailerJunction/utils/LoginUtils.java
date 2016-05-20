package com.example.mridul.RetailerJunction.utils;

import android.content.Context;

import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;

/**
 * Created by Mridul on 5/20/2016.
 */
public class LoginUtils {

    public static boolean isLoggedIn(Context c) {

        if (PreferencesHelper.getInstance(c).getLoginState() == true &&
            PreferencesHelper.getInstance(c).getToken() != null) {
            return true;
        }
        return false;
    }
}
