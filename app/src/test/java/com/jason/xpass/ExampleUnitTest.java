package com.jason.xpass;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.util.AES;
import com.jason.xpass.util.RSA;
import com.jason.xpass.util.codec.Base64;

import org.junit.Test;

import java.util.HashMap;
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
    public void RSA_encryptByPublicKey() throws Exception {

        Map<String, Object> keyMap = RSA.genKeyPair();

        String publicKey = RSA.getPublicKey(keyMap);
        String privateKey = RSA.getPrivateKey(keyMap);

        String src = "{\"id\":1}";
        String encryptData = Base64.encodeBase64String(
                RSA.encryptByPublicKey(src.getBytes(), publicKey));

//        String encryptData = "v1RL3QPZCvRC9KdlXAgQBgzayfZSYlMfy6Kz0VPTOaIjRk3P/qVODEfOebVnKok/abQRx1qPXaZHR9kiJkyw2Aw7Zgly5XPK9Cx9dEtNifAgtyvRPV4rILxCUoY97Y7yBtOHcCfC7Dr3MoiOnZI/qvxHbgKFjBqGUVPNNyC6/iWbboZ0Tofis1cyWeYRMOPha+YY7+bOT3h0rU9XY5Aen+H4ac+OfXKYlkXjipMTmAm5FUhgX62GU4qHNnZlkP3N8r1O6zCFtX6K3VVpFHRlMOU0S8litJ/1PHWg61kWyUq/IL8Av/PrKAXeY0WQnrl7uharlcy3XZ/N1cqh4FNYxohF872rZ1htpWIER65m14QTWhGWNpnzfg6AMlUJUXAaIf2vvmvsebQbmhrFN+EV+lXtb6odigJQBiPX/kOu35U1VwteHpN1+AexF7nQuzQX8FlWsyZ1I0cmO4Wm4DR98Xw5+jS2tNDiMEWtfAl66k525oMbQSwl7tEg9JYf/EViVBZyT6DoZyLyspRsnYCmXZdgOd+2YL96FwHLRk42wNc=";

        String res = new String(RSA.decryptByPrivateKey(Base64.decodeBase64(encryptData), privateKey));
        System.out.println(res);
    }

    @Test
    public void RSA_encryptByPrivateKey() throws Exception {

        String publicKey = RSA.getPublicKey(null);
        String privateKey = RSA.getPrivateKey(null);

        String src = "{\"id\":1}";
        String encryptData = Base64.encodeBase64String(
                RSA.encryptByPrivateKey(src.getBytes(), privateKey));

        String res = new String(RSA.decryptByPublicKey(Base64.decodeBase64(encryptData), publicKey));
        assertTrue(src.equals(res));
    }

    @Test
    public void allEncrypt() throws Exception {
        Map<String, Object> pair = RSA.genKeyPair();
        Map<String, Object> content = new HashMap<>();
        String privateKey = RSA.getPrivateKey(pair);
        String publicKey = RSA.getPublicKey(pair);
        content.put("publicKey", publicKey);

        String info = new String(Base64.encodeBase64(
                RSA.encryptByPrivateKey(
                        JSON.toJSONString("1").getBytes(),
                        privateKey)));
        content.put("info", info);

//        String src = JSON.toJSONString(content);
//        System.out.println(src);

        String encryptInfo = AES.encrypt(JSON.toJSONString(content));
        System.out.println(encryptInfo);

        String revert = AES.decrypt(encryptInfo);
        System.out.println(revert);
    }

}