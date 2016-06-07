package com.jason.xpass.util;

import com.jason.xpass.util.codec.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Description:
 * <p/>
 * Created by js.lee on 5/9/16.
 */
public class RSA {

    /**
     * Encrypt algorithm
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * Sign algorithm
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * Max encrypt block
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * Max decrypt block
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** */
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * Sign data by private key
     *
     * @param data       data that have been encrypted
     * @param privateKey private key(BASE64 encoding)
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * Check sign
     *
     * @param data      data that have been encrypted
     * @param publicKey public key(BASE64 encoding)
     * @param sign      sign
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * Decrypt by private key
     *
     * @param encryptedData data that that have encrypted by public key
     * @param privateKey    private key(BASE64 encoding)
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        return decrypt(encryptedData, privateKey, 1);
    }

    /**
     * Decrypt by public key
     *
     * @param encryptedData data that that have encrypted by private key
     * @param publicKey     public key(BASE64 encoding)
     * @return
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        return decrypt(encryptedData, publicKey, 2);
    }


    /**
     * Encrypt by public key
     *
     * @param data      original data
     * @param publicKey public key(BASE64 encoding)
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        return encrypt(data, publicKey, 2);
    }

    /**
     * Encrypt by private key
     *
     * @param data       original data
     * @param privateKey private key(BASE64 encoding)
     * @return
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        return encrypt(data, privateKey, 1);
    }

    private static byte[] decrypt(byte[] encryptedData, String keyStr, int keyFlag) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(keyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key key = null;
        Cipher cipher = null;
        if (keyFlag == 1) {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(pkcs8KeySpec);
            /**
             * I have cost much time on it. It works well on a android machine after replace "RSA" to
             * "RSA/ECB/PKCS1Padding". but can not get a expected value when this piece of code is
             * run in a junit test or a main method.
             * Because the default cipher initialization padding appears to be different between
             * the desktop JVM and the Android JVM.
             *
             * reference: http://stackoverflow.com/questions/6069369/rsa-encryption-difference-between-java-and-android
             */
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        } else if (keyFlag == 2) {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(x509KeySpec);
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        }

        cipher.init(Cipher.DECRYPT_MODE, key);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache = null;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i += 1;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] encrypt(byte[] data, String keyStr, int keyFlag) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(keyStr);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key key = null;
        Cipher cipher = null;
        if (keyFlag == 1) {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(pkcs8KeySpec);
            /**
             * I have cost much time on it. It works well on a android machine after replace "RSA" to
             * "RSA/ECB/PKCS1Padding". but can not get a expected value when this piece of code is
             * run in a junit test or a main method.
             * Because the default cipher initialization padding appears to be different between
             * the desktop JVM and the Android JVM.
             *
             * reference: http://stackoverflow.com/questions/6069369/rsa-encryption-difference-between-java-and-android
             */
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } else if (keyFlag == 2) {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(x509KeySpec);
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        }

        cipher.init(Cipher.ENCRYPT_MODE, key);

        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache = null;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i += 1;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
//        Key key = (Key) keyMap.get(PRIVATE_KEY);
//        return Base64.encodeBase64String(key.getEncoded());
        return "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANurtQFKXJzRpiQg" +
                "x0DecYNSMEc6JRZ5LzgRhTDHPJlnb4i1UjI4S/GHv3He9FKbD1bYapR91rrRXFP7" +
                "Qkm73Zrc5ViOONq0x8P1JKx8+YUlFhzCaWOFHSdv6Gh3a40bEPFZJ69gKzY2PgfW" +
                "ir8/40qzG0p8qYiGeZDw8E/IWBdRAgMBAAECgYEA1ix2gPiYjUkWnFjdDFEVCX1j" +
                "lr6JFH043YivfFx0p/iiVP68UjxzRt0cehBv0+5cqUa9u2NpraGcTEFIYw1ow9sm" +
                "UBoxbvQsm9mRTB1Ut2PvJXinFNb1z+bdT3RYU3tADNUn7MIgsqvyZWH2WUZXMZgu" +
                "qXzGklEZDuTE3tmg2fECQQDxeTT+plmt8O2DbSoR/JCPs5st0vcDCSKXGhTeDjBw" +
                "suAAeH0Wc8gcFdV2tESbArwzNIhKjLe6d5hGm+RG90PdAkEA6OK6j9OYn0xe6DkU" +
                "SUItCSk1VaLVeAgLUZA+0KR9ODavHZH4QRzj4pluADqglyIYKN9P2ZgHj/cvn1tn" +
                "muiUBQJAaqQ72kZ/Dol7a3J3hPAEq+IHI0qrGiUbqJ21H4gmrm7g7HRJ0fOaKYUe" +
                "+8iLD+Y6VWba1gmlTm1oy64nN4wV5QJAS9ItfVAdu5douuCCi0thUD87XxMxvu+X" +
                "h8mXueQj5J5hKxZwJfra8taTKr3rtOjjxsLVw3ks1SFcPtzKgXPBZQJBALiUQJII" +
                "mEVsbF0SyQMk/4OnPf4urGuJYdKr4bX/j5I779t7yKZTRipm2N14KLDs3qPAKnYc" +
                "LXkL138RiPfXDNM=";
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
//        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        return Base64.encodeBase64String(key.getEncoded());
        return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbq7UBSlyc0aYkIMdA3nGDUjBHOiUWeS84EYUwxzyZZ" +
                "2+ItVIyOEvxh79x3vRSmw9W2GqUfda60VxT+0JJu92a3OVYjjjatMfD9SSsfPmFJRYcwmljhR0nb+hod2u" +
                "NGxDxWSevYCs2Nj4H1oq/P+NKsxtKfKmIhnmQ8PBPyFgXUQIDAQAB";
    }

}
