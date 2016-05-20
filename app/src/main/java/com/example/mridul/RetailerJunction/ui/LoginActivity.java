package com.example.mridul.RetailerJunction.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mridul.RetailerJunction.daogenerator.model.CloudAppDetails;
import com.example.mridul.RetailerJunction.daogenerator.model.InstallRecords;
import com.example.mridul.RetailerJunction.helpers.PreferencesHelper;
import com.example.mridul.RetailerJunction.utils.CloudAppInfoObject;
import com.example.mridul.RetailerJunction.utils.Constants;
import com.example.mridul.helloworld.R;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mridul on 5/20/2016.
 */
public class LoginActivity extends Activity {

    static Context mContext;

    void ShowToast (String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.login_activity);

        Button b = (Button) findViewById(R.id.login_button);

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


                LoginTask l = new LoginTask();
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username", username);
                data.put("password", password);

                l.execute(data);
            }
        });
    }

    public class LoginTask extends AsyncTask<HashMap, Void, String> {

        protected void onPostExecute(String token) {
            if (token == null) {
                ShowToast("Incorrect username or password");
                return;
            }
            PreferencesHelper.getInstance(mContext).saveToken(token);
            PreferencesHelper.getInstance(mContext).saveLoginState(true);

            ShowToast("Login Successful");


            startActivity(new Intent(mContext, RetailerJunctionActivity.class));

            finish();
        }

        @Override
        protected String doInBackground(HashMap...data) {

            String token = null;
            try
            {
                String URL = Constants.APPSLIST_LOGIN_URL;
                HttpPost httpPost = new HttpPost(URL);
                HttpClient client = new DefaultHttpClient();
                String responseText;

                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("email", (String) data[0].get("username")));
                postParameters.add(new BasicNameValuePair("password", (String) data[0].get("password")));

                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

                HttpResponse response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() != 200) {
                    Log.e("Login Failed: ", response.toString());
                    return null;
                }

                InputStream is  = response.getEntity().getContent();
                responseText = IOUtils.toString(is);

                JSONObject json = new JSONObject(responseText);
                token = (String) json.get("token");

            } catch(Exception ex) {
                Log.e("Fetch appslist Failed", ex.getMessage());
                return null;
            }
            return token;
        }
    }
}
