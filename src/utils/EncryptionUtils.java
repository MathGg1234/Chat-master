package utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class EncryptionUtils {

    public static SecretKey loadKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static SecretKey loadKeyFromString(String keyString) {
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        return loadKeyFromBytes(keyBytes);
    }

    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = java.util.Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    }
}