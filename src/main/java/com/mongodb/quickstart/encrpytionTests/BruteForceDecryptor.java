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
    public String decryptAES(String encryptedData, int maxAttempts) {
        return decrypt(encryptedData, maxAttempts, new AESEngine(), 16); // AES uses 128-bit keys
    }

    /**
     * try to decrypt 3DES encrypted data using brute force within limited attempts.
     * @param encryptedData The encrypted data in hexadecimal string format.
     * @param maxAttempts   Maximum number of brute force attempts.
     * @return The decrypted text if successful, null otherwise.
     */
    public String decrypt3DES(String encryptedData, int maxAttempts) {
        return decrypt(encryptedData, maxAttempts, new DESedeEngine(), 24); // 3DES uses 192-bit keys (24 bytes)
    }

    /**
     * TODO: change to check if it is the original password!!
     * @param text
     * @return
     */
    private boolean isValidDecryptedText(String text) {
        return text.matches("\\A\\p{ASCII}*\\z");
    }

    /**
     * general decrypt method used by both AES and 3DES decryption methods.
     * @param encryptedData The encrypted data in hexadecimal string format.
     * @param maxAttempts   Maximum number of brute force attempts.
     * @param engine        The cipher engine (AES or 3DES).
     * @param keySize       The size of the key in bytes.
     * @return the decrypted text if successful, null otherwise.
     */
    private String decrypt(String encryptedData, int maxAttempts, org.bouncycastle.crypto.BlockCipher engine, int keySize) {
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
                if (isValidDecryptedText(decryptedText)) {
                    // successfully decrypted text
                    return decryptedText;
                }
            } catch (CryptoException e) {
                // ignoring the pad block corrupted exception or any decryption error
                // since this is common in brute force attacks (like what is being simulated here)

                // skips decryption errors - since randomly generated keys won't work
            }
        }
        return null; // Return null if no valid decryption found
    }


}
