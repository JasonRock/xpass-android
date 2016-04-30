package com.jason.xpass.http;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.util.AES;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public abstract class XCallBack<T> implements Callback {

    private Class<T> clazz;

    public XCallBack() {
        // Get actual Class Object when runtime.
        this.clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//        Headers responseHeaders = response.headers();
//        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//        }

        TransportResponse transportResponse = JSON.parseObject(response.body().string(), TransportResponse.class);
        String info = transportResponse.getInfo();
        // Decrypt the info
        String src = AES.decrypt(info);
        onResponse(JSON.parseObject(src, clazz));
    }

    public abstract void onResponse(T t);
}
