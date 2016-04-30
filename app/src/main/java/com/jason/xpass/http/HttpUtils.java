package com.jason.xpass.http;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class HttpUtils {

    public static void get(String url, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

}
