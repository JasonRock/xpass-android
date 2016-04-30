package com.jason.xpass.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class CryptoEncryption {

    public static byte[] encrypt(byte[] src, String secret, String algorithmName) {
        try {
            Key secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName);
            Cipher encipher = Cipher.getInstance("AES" + "/ECB/PKCS5Padding");
            encipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return encipher.doFinal(src);
        } catch (Exception e) {
            return null;
        }

    }

    public static byte[] decrypt(byte[] src, String secret, String algorithmName) {
        try {
            Key secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName);
            Cipher encipher = Cipher.getInstance("AES" + "/ECB/PKCS5Padding");
            encipher.init(Cipher.DECRYPT_MODE, secretKey);
            return encipher.doFinal(src);
        } catch (Exception e) {
            return null;
        }
    }

}
