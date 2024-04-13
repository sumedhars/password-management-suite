package com.mongodb.quickstart;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TripleDESEncryptor {
    private SecretKey secretKey;
    private Cipher cipher;

    public TripleDESEncryptor() {
        try {
            // Generate a SecretKey for 3DES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede"); // "DESede" - another name for Triple DES
            keyGenerator.init(168); // 168-bit usually used for 3DES
            this.secretKey = keyGenerator.generateKey();

            // create Cipher instance for 3DES
            this.cipher = Cipher.getInstance("DESede");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String encrypt(String input) {
        try {
            // Initialize Cipher for ENCRYPTION mode
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] inputBytes = input.getBytes();
            byte[] encryptedBytes = cipher.doFinal(inputBytes);
            return java.util.Base64.getEncoder().encodeToString(encryptedBytes); // Base64 encode to make it a readable string
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedInput) {
        try {
            // Initialize Cipher for DECRYPTION mode
            byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedInput);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            TripleDESEncryptor encryptor = new TripleDESEncryptor();
            String original = "hello";
            String encrypted = encryptor.encrypt(original);
            String decrypted = encryptor.decrypt(encrypted);

            System.out.println("Original: " + original);
            System.out.println("Encrypted: " + encrypted);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
