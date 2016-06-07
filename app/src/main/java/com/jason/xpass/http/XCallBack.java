package com.jason.xpass.http;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.util.RSA;
import com.jason.xpass.util.codec.Base64;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public abstract class XCallBack implements Callback {

    protected ThreadLocal<Map<String, Object>> keyPair;

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        TransportResponse transportResponse = JSON.parseObject(response.body().string(), TransportResponse.class);
        String info = transportResponse.getInfo();

        try {
            String src = new String(RSA.decryptByPrivateKey(Base64.decodeBase64(info), RSA.getPrivateKey(keyPair.get())));
            onResponse(src);
        } catch (Exception e) {
            e.printStackTrace();
            onResponse(null);
        }
    }

    public void setKeyPair(Map<String, Object> keyPair) {
        this.keyPair = new ThreadLocal<Map<String, Object>>();
        this.keyPair.set(keyPair);
    }

    public abstract void onResponse(String json);
}
