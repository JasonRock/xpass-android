package com.jason.xpass.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class HttpUtils {

    private static final String HOST = "http://192.168.0.107:9000";

    public static void get(String url, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(HOST + url)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

    public static void post(String url, Object data, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(HOST + url)
                    .post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(data)))
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

}
