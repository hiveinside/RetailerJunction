package com.example.mridul.RetailerJunction.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.example.mridul.RetailerJunction.utils.Constants;
import com.example.mridul.helloworld.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by Mridul on 5/20/2016.
 */
public class LoginActivity extends AppCompatActivity {

    static Context mContext;
    private View mProgressView;
    private View mLoginFormView;

    void ShowToast (String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    void ShowToastFromThread (final String mytext) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mytext, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.login_activity);

        Button b = (Button) findViewById(R.id.login_button);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username_field = (EditText) findViewById(R.id.username);
                EditText password_field = (EditText) findViewById(R.id.password);

                String username = username_field.getText().toString();
                String password = password_field.getText().toString();

                if (username == null || password == null) {
                    ShowToast("Wrong username or password");
                    return;
                }
                showProgress(true);

                LoginTask l = new LoginTask();
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username", username);
                data.put("password", password);

                l.execute(data);
            }
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public class LoginTask extends AsyncTask<HashMap, Void, String> {

        protected void onPostExecute(String token) {
            showProgress(false);

            if (token == null) {
                return;
            }

            ShowToast("Login Successful");

            startActivity(new Intent(mContext, RetailerJunctionActivity.class));

            finish();
        }

        @Override
        protected String doInBackground(HashMap...data) {

            String token = null;
            try
            {
                URL loginURL = new URL(Constants.APPSLIST_LOGIN_URL);

                String postData =   "email=" + data[0].get("username") +
                                    "&password=" + data[0].get("password");

                HttpURLConnection conn = (HttpURLConnection) loginURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setUseCaches(false);

                byte[] postDataBytes = postData.getBytes("UTF-8");
                conn.getOutputStream().write(postDataBytes);

                int responseCode = conn.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    ShowToastFromThread("Incorrect username or password");
                    return null;
                }

                String responseText = "";
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseText += line;
                }

                JSONObject json = new JSONObject(responseText);
                token = (String) json.get("token");

                PreferencesHelper.getInstance(mContext).saveLoginState(true);
                PreferencesHelper.getInstance(mContext).saveToken(token);
                PreferencesHelper.getInstance(mContext).saveLoginID((String)data[0].get("username"));

            } catch(UnknownHostException ex) {
                ShowToastFromThread("No internet connection");
                return null;

            } catch(Exception ex) {
                ShowToastFromThread("Unable to login");
                Log.e("Login exception", ex.getMessage());
                return null;
            }
            return token;
        }
    }
}
