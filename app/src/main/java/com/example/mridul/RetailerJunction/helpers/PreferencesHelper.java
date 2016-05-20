package com.example.mridul.RetailerJunction.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mridul on 5/20/2016.
 */
public class PreferencesHelper {
    private static final String PHONE_HELPER = "Phone_Helper";
    private static final String LOGIN_STATE = "Login_State";
    private static final String UPDATE_CHECK = "Update_Check";
    private static final String TOKEN = "token";

    private static Context context = null;
    private static PreferencesHelper pHelper = null;

    private PreferencesHelper(Context mContext) {
        context = mContext.getApplicationContext();
    }

    public static synchronized PreferencesHelper getInstance(Context mContext) {
        PreferencesHelper preferencesHelper;
        synchronized (PreferencesHelper.class) {
            if (pHelper == null && mContext != null) {
                pHelper = new PreferencesHelper(mContext);
            }
            preferencesHelper = pHelper;
        }
        return preferencesHelper;
    }

    public String getToken() {
        return context.getSharedPreferences(PHONE_HELPER, 0).getString(TOKEN, null);
    }

    public void saveToken(String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PHONE_HELPER, 0).edit();
        editor.putString(TOKEN, name);
        editor.commit();
    }

    public boolean getLoginState() {
        return context.getSharedPreferences(PHONE_HELPER, 0).getBoolean(LOGIN_STATE, false);
    }

    public void saveLoginState(boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PHONE_HELPER, 0).edit();
        editor.putBoolean(LOGIN_STATE, state);
        editor.commit();
    }
}
