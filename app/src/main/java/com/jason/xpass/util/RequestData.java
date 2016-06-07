package com.jason.xpass.util;

import java.util.LinkedHashMap;

/**
 * Description:
 * <p/>
 * Created by js.lee on 6/3/16.
 */
public class RequestData extends LinkedHashMap<String, Object> {

    public RequestData append(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
