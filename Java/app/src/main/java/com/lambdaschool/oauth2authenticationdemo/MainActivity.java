package com.lambdaschool.oauth2authenticationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String auth = Base64.encodeToString("lambda-client:lambda-secret".getBytes(), Base64.DEFAULT);

                Map<String, String> headerProperties = new HashMap<>();
                headerProperties.put("Authorization", "Basic " + auth);

                /*String tokenRequest = NetworkAdapter.httpRequest(
                        "http://10.0.2.2:8080/oauth/token?grant_type=password&username=sally&password=password&scope=",
                        "POST", null, headerProperties);*/

                String tokenRequest = NetworkAdapter.httpRequest(
                        "http://10.0.2.2:8080/oauth/token?grant_type=password&username=lucy&password=ILuvM4th&scope=",
                        "POST", null, headerProperties);

                Log.i(TAG, tokenRequest);
                try {
                    String token = new JSONObject(tokenRequest).getString("access_token");

                    headerProperties.clear();
                    headerProperties.put("Authorization", "Bearer " + token);

                    String result = NetworkAdapter.httpRequest("http://10.0.2.2:8080/students/students", "GET", null, headerProperties);
                    Log.i("Authentication", result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
