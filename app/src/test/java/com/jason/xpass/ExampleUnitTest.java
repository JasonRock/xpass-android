package com.jason.xpass;

import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.util.RSA;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void http_test() throws Exception {

        Map<String, Object> pair = RSA.genKeyPair();
        System.out.println(RSA.getPrivateKey(pair));
        System.out.println(RSA.getPublicKey(pair));
        assertTrue(true);
    }

}