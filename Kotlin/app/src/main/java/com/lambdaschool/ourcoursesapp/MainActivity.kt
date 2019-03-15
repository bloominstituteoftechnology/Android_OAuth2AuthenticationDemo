package com.lambdaschool.ourcoursesapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainScope().launch {
            withContext(Dispatchers.IO) {
                val auth = Base64.encodeToString("lambda-client:lambda-secret".toByteArray(), Base64.DEFAULT)

                val tokenRequest = NetworkAdapter.httpRequest(
                    "http://10.0.2.2:8080/oauth/token?grant_type=password&username=sally&password=password&scope=", "POST",
                    headerProperties = mapOf(
                        "Authorization" to "Basic $auth"
                    )
                )
                println(tokenRequest)
                val token = JSONObject(tokenRequest).getString("access_token")
                val result = NetworkAdapter.httpRequest(
                    "http://10.0.2.2:8080/students/students", "GET",
                    headerProperties = mapOf(
                        "Authorization" to "Bearer $token",
                        "Content-Type" to "application/json",
                        "Accept" to "application/json"
                    )
                )
                Log.i("Authentication", result)
            }
        }
    }
}
