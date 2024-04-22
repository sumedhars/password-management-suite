package com.mongodb.quickstart.encrpytionTests;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.HashMap;

public class BruteForceDecryptor {

    /**
     * try to decrypt AES encrypted data using brute force within limited attempts.
     * @param encryptedData The encrypted data in hexadecimal string format.
     * @param maxAttempts   Maximum number of brute force attempts.
     * @return The decrypted text if successful, null otherwise.
     */
    public String decryptAES(String encryptedData, int maxAttempts, String plaintext) {
        return decrypt(encryptedData, maxAttempts, new AESEngine(), 16, plaintext); // AES uses 128-bit keys
    }

    /**
     * try to decrypt 3DES encrypted data using brute force within limited attempts.
     * @param encryptedData The encrypted data in hexadecimal string format.
     * @param maxAttempts   Maximum number of brute force attempts.
     * @return The decrypted text if successful, null otherwise.
     */
    public String decrypt3DES(String encryptedData, int maxAttempts, String plaintext) {
        return decrypt(encryptedData, maxAttempts, new DESedeEngine(), 24, plaintext); // 3DES uses 192-bit keys (24 bytes)
    }


    /**
     * general decrypt method used by both AES and 3DES decryption methods.
     * @param encryptedData The encrypted data in hexadecimal string format.
     * @param maxAttempts   Maximum number of brute force attempts.
     * @param engine        The cipher engine (AES or 3DES).
     * @param keySize       The size of the key in bytes.
     * @return the decrypted text if successful, null otherwise.
     */
    private String decrypt(String encryptedData, int maxAttempts, org.bouncycastle.crypto.BlockCipher engine,
                           int keySize, String plaintext) {
        byte[] cipherData = Base64.decode(encryptedData);
        byte[] keyBytes = new byte[keySize];
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            for (int i = 0; i < keyBytes.length; i++) {
                // randomly generate each byte of the key
                keyBytes[i] = (byte) (Math.random() * 256);
            }

            try {
                cipher.init(false, new KeyParameter(keyBytes));
                byte[] decryptedData = new byte[cipher.getOutputSize(cipherData.length)];
                int processed = cipher.processBytes(cipherData, 0, cipherData.length, decryptedData, 0);
                processed += cipher.doFinal(decryptedData, processed);

                String decryptedText = new String(decryptedData, 0, processed).trim();
                // System.out.println(decryptedText);
                if (decryptedText.equals(plaintext)) {
                    return decryptedText;
                }
            } catch (CryptoException e) {
                // ignoring the pad block corrupted exception or any decryption error
                // since this is common in brute force attacks (like what is being simulated here)

                // skips decryption errors - since randomly generated keys won't work
            }
        }
        return null; // return null if no valid decryption found
    }


    /**
     * try to decrypt data encrypted with Caesar cipher using brute force.
     * @param encryptedData encrypted data as String.
     * @param plaintext the expected plaintext to find the correct shift.
     * @return decrypted text if successful, null otherwise.
     */
    public String decryptCaesar(String encryptedData, String plaintext) {
        for (int shift = 1; shift < 26; shift++) {
            String decryptedText = decryptWithShift(encryptedData, shift);
            if (decryptedText.equals(plaintext)) {
                return decryptedText;
            }
        }
        return null; // return null if no valid decryption found
    }

    /**
     * decrypts the text using a given shift according to caesar cipher rules
     * @param text The encrypted text.
     * @param shift The shift used to decrypt.
     * @return The decrypted text.
     */
    private String decryptWithShift(String text, int shift) {
        StringBuilder decrypted = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (character >= 'a' && character <= 'z') {
                char shifted = (char) (character - shift);
                if (shifted < 'a') {
                    shifted += 26;
                }
                decrypted.append(shifted);
            } else if (character >= 'A' && character <= 'Z') {
                char shifted = (char) (character - shift);
                if (shifted < 'A') {
                    shifted += 26;
                }
                decrypted.append(shifted);
            } else {
                decrypted.append(character); // non-alphabetic characters are not shifted
            }
        }
        return decrypted.toString();
    }


}
