package com.jason.xpass.util;

import android.util.Base64;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class DES {

    private static final String DES_KEY = "01234567";

    public static String encrypt(String src) {
        byte[] b = CryptoEncryption.encrypt(src.getBytes(), DES_KEY, "DES");
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String decrypt(String src) {
        byte[] b = CryptoEncryption.decrypt(Base64.decode(src, Base64.DEFAULT), DES_KEY, "DES");
        if (b == null) {
            return null;
        }
        return new String(b);
    }
}
