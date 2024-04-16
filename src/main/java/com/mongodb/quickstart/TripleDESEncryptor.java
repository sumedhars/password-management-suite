package com.mongodb.quickstart;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class TripleDESEncryptor implements Encryptor{
    private SecretKey secretKey;

    public TripleDESEncryptor() {
        try {
            // Generate a SecretKey for 3DES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede"); // "DESede" - another name for Triple DES
            keyGenerator.init(168); // 168-bit usually used for 3DES
            this.secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TripleDESEncryptor(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encrypt(String plaintext) {
        try {
            // Create Cipher instance for 3DES
            Cipher cipher = Cipher.getInstance("DESede");
            // Initialize Cipher for ENCRYPTION mode
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] inputBytes = plaintext.getBytes();
            byte[] encryptedBytes = cipher.doFinal(inputBytes);
            return java.util.Base64.getEncoder().encodeToString(encryptedBytes); // Base64 encode to make it a readable string
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        try {
            // Create Cipher instance for 3DES
            Cipher cipher = Cipher.getInstance("DESede");
            byte[] encryptedBytes = java.util.Base64.getDecoder().decode(ciphertext);
            // Initialize Cipher for DECRYPTION mode
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecretKey getSecretKey(){
        return secretKey;
    }

    protected void setSecretKey(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    public static String toString(SecretKey secretKey) {
        // Encode the SecretKey bytes to Base64 to get a String representation
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey stringToSecretKey(String keyString) {
        try {
            // Decode the Base64 String to get the key bytes
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            // Reconstruct the key using the appropriate algorithm and the key bytes
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "DESede");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null; // handle Base64 decoding error or other issues
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

            String keyString = toString(encryptor.secretKey);
            SecretKey secretKey = stringToSecretKey(keyString);
            System.out.println(secretKey.equals(encryptor.secretKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
