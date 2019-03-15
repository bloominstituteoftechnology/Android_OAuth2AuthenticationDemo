package com.lambdaschool.ourcoursesapp

import android.support.annotation.WorkerThread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object NetworkAdapter {
    @WorkerThread
    suspend fun httpRequest(urlString: String, requestType: String, content: String? = null, headerProperties: Map<String, String>? = null): String {
        var result = ""
        var stream: InputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            //                    connection.setRequestProperty("x-api-key", API_KEY);
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.readTimeout = 3000
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.connectTimeout = 3000
            // For this use case, set HTTP method to GET.
            connection.requestMethod = requestType

            if (headerProperties != null) {
                for((key, value) in headerProperties) {
                    connection.setRequestProperty(key, value)
                }
            }

            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.doInput = true

            if (requestType == "POST" && content != null) {
                val outputStream = connection.outputStream
                outputStream.write(content.toString().toByteArray())
                outputStream.close()
            } else {
                // Open communications link (network traffic occurs here).
                connection.connect()
            }
            //            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            val responseCode = connection.responseCode
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                result = Integer.toString(responseCode)
                throw IOException("HTTP error code: $responseCode")
            }
            // Retrieve the response body as an InputStream.
            stream = connection.inputStream
            // publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                val reader = BufferedReader(InputStreamReader(stream))
                val builder = StringBuilder()
                var line: String? = reader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }
                result = builder.toString()
//                success = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            connection?.disconnect()

        }

        /*try {
            downloadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            callback.onRequestFinished(false, result[0]);
        }*/

        return result
    }
}