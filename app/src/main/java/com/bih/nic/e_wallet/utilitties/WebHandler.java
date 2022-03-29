package com.bih.nic.e_wallet.utilitties;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chandan kumar on 11-03-2018.
 */
public class
WebHandler {
    HttpURLConnection conn;

    public static String callByPost(final String reqJson, String urls) {
        InputStream inputStream = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(urls.trim());
            httpPost.setEntity(new StringEntity(reqJson, HTTP.UTF_8));
            httpPost.setHeader("Content-type", "application/json");
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            result = null;

        }
        return result;

    }

    public static String callByPostwithoutparameter(String urls) {
        InputStream inputStream = null;
        String result = null;
        Log.e("log",urls);
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls);
            httpPost.setHeader("Content-type", "text/plain");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            result = null;

        }
        return result;

    }

   /* public static String callByPost(final String reqJson, String urls){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new Def aultHttpClient();
            HttpPost httpPost = new HttpPost(urls);
            //  StringEntity se = new StringEntity(reqJson);
            httpPost.setEntity(new StringEntity(reqJson, HTTP.UTF_8));
            // StringEntity se = new StringEntity(reqJson);
            // httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = null;

        } catch (Exception e) {
            e.printStackTrace();
            result = null;

        }
        return result;

    }*/

    public static String callByGet(String urls) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(urls));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

        return result;
    }
    public static String postwwwURL(final String postParameters, String urls) throws MalformedURLException {
        String res = null;
        try {
            URL urlToRequest = new URL(urls);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            //int statusCode = urlConnection.getResponseCode();
            //if (statusCode ==  200) {
                res = convertInputStreamToString(urlConnection.getInputStream());
            //}
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return res;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}


