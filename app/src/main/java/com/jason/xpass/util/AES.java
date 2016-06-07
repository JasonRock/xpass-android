package com.jason.xpass.util;

import com.jason.xpass.util.codec.Base64;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class AES {

    private static final String AES_KEY = "0123456789012345";

    public static String encrypt(String src) {
        byte[] b = CryptoEncryption.encrypt(src.getBytes(), AES_KEY, "AES");
        return Base64.encodeBase64String(b);
    }

    public static String decrypt(String src) {
        byte[] b = CryptoEncryption.decrypt(Base64.decodeBase64(src), AES_KEY, "AES");
        if (b == null) {
            return null;
        }
        return new String(b);
    }
}
