package com.mongodb.quickstart;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * code reference resources:
 * 1) https://www.baeldung.com/java-aes-encryption-decryption
 * 2) https://www.webscale.com/engineering-education/implementing-aes-encryption-and-decryption-in-java/
 * 3) https://www.geeksforgeeks.org/what-is-java-aes-encryption-and-decryption/
 */
public class AESEncryptor implements Encryptor {

    private static final String AES = "AES";
    private static final int KEY_SIZE = 16; // 128 bits

    private SecretKeySpec secretKey;

    public AESEncryptor() {
        this.secretKey = generateAESKey();
    }

    public AESEncryptor(SecretKeySpec secretKey) {
        this.secretKey = secretKey;
    }

    private SecretKeySpec generateAESKey() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[KEY_SIZE];
            secureRandom.nextBytes(keyBytes);
            return new SecretKeySpec(keyBytes, AES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String encrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecretKeySpec getSecretKey(){
        return secretKey;
    }

    protected void setSecretKey(SecretKeySpec secretKey){
        this.secretKey = secretKey;
    }

    public static String toString(SecretKeySpec secretKey){
        // convert SecretKey to byte array
        byte[] keyBytes = secretKey.getEncoded();
        // encode the byte array to a Base64 String
        String keyString = Base64.getEncoder().encodeToString(keyBytes);
        return keyString;
    }

    public static SecretKeySpec stringToSecretKey(String keyString){
        // decode the Base64 string to a byte array
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        // create SecretKey from the byte array
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        return secretKey;
    }

    public static void main(String[] args) {
        AESEncryptor aesEncryptor = new AESEncryptor(AESEncryptor.stringToSecretKey("Ks4aWFsyiZjHYRr+fCPlYrCoGgFzFptP"));
        String plaintext = "123";
        System.out.println("Original: " + plaintext);
        String encrypted = aesEncryptor.encrypt(plaintext);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = aesEncryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        String keyString = toString(aesEncryptor.secretKey);
        SecretKeySpec secretKeySpec = stringToSecretKey(keyString);
        System.out.println(secretKeySpec.equals(aesEncryptor.secretKey));
    }
}
