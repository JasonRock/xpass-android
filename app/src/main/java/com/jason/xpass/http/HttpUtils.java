package com.jason.xpass.http;

import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.util.AES;
import com.jason.xpass.util.RSA;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class HttpUtils {

    private static final String HOST = "http://192.168.0.107:9000";

    protected static ThreadLocal<Map<String, Object>> keyPair;

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

    public static void post(String url, Callback callback) {
        post(url, null, callback);
    }

    public static void post(String url, Object data, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();

            // Generate RSA key pair
            Map<String, Object> pair = RSA.genKeyPair();
            keyPair = new ThreadLocal<>();
            keyPair.set(pair);

            Map<String, Object> info = new HashMap<>();
            String privateKey = RSA.getPrivateKey(pair);
            String publicKey = RSA.getPublicKey(pair);
            info.put("publicKey", publicKey);

            if (data != null) {
                String content = Base64.encodeToString(RSA.encryptByPrivateKey(Base64.decode(JSON.toJSONString(data),
                        Base64.DEFAULT), privateKey), Base64.DEFAULT);
                info.put("content", content);
                String result = Base64.encodeToString(
                        RSA.decryptByPublicKey(Base64.decode(content, Base64.DEFAULT), publicKey),
                        Base64.DEFAULT);
            }
            String encryptInfo = AES.encrypt(JSON.toJSONString(info));

            Request request = new Request.Builder()
                    .url(HOST + url)
                    .post(RequestBody.create(MediaType.parse("application/json"), encryptInfo))
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

}
