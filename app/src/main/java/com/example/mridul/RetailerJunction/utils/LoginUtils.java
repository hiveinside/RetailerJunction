package com.example.mridul.RetailerJunction.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static void doServerLogout(String token) {

        ServerLogout s = new ServerLogout();
        s.execute(token);
    }

    private static class ServerLogout extends AsyncTask<String, Void, Boolean> {

        protected void onPostExecute(Boolean result) {

        }

        @Override
        protected Boolean doInBackground(String... args) {

            try {
                String auth_token = args[0];

                if (auth_token == null){
                    return null;
                }

                String urlString = Constants.APPSLIST_LOGOUT_URL + "?token=" + auth_token;
                java.net.URL submitURL = new URL(urlString);

                HttpURLConnection conn = (HttpURLConnection) submitURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "close");

                String response = "";
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                Log.e("ServerLogout", response);

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}
