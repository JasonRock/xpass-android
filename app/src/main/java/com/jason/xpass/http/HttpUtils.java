package com.jason.xpass.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.util.AES;
import com.jason.xpass.util.RSA;
import com.jason.xpass.util.codec.Base64;

import java.util.HashMap;
import java.util.Map;

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

        private static final String HOST = "http://108.61.126.193:9000";
//    private static final String HOST = "http://192.168.1.27:9000";

    public static void get(String url, XCallBack callback) {
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

    public static void post(String url, XCallBack callback) {
        post(url, null, callback);
    }

    public static void post(String url, Object data, XCallBack callback) {
        try {
            OkHttpClient client = new OkHttpClient();

            // Generate RSA key pair
            Map<String, Object> pair = RSA.genKeyPair();
            Map<String, Object> content = new HashMap<>();
            String privateKey = RSA.getPrivateKey(pair);
            String publicKey = RSA.getPublicKey(pair);
            content.put("publicKey", publicKey);

            if (data != null) {
                String info = new String(Base64.encodeBase64(
                        RSA.encryptByPrivateKey(
                                JSON.toJSONString(data).getBytes(),
                                privateKey)));
                content.put("info", info);
            }
            String encryptInfo = AES.encrypt(JSON.toJSONString(content));

            Request request = new Request.Builder()
                    .url(HOST + url)
                    .post(RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"), encryptInfo))
                    .build();

            callback.setKeyPair(pair);
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

}
