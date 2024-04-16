package com.mongodb.quickstart;

import java.security.SecureRandom;
import java.util.Random;

public class CaesarCipher {
    private int shift;
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public CaesarCipher(int shift) {
        this.shift = shift;
    }

    /**
     * creates a random alphanumeric string of a specified length to be used as salt
     * @param length of salt
     * @return salt generated
     */
    private String generateSalt(int length) {
        Random random = new SecureRandom();
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARS.length());
            salt.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return salt.toString();
    }

    /**
     * produces hash
     * if salt is used, then it prepends it to the plaintext
     * @param plaintext
     * @param useSalt boolean that indicates if salt is used or not
     * @return hash
     */
    public String encrypt(String plaintext, boolean useSalt) {
        // Generate an 8-character salt if salting is used
        String salt = useSalt ? generateSalt(8) : "";
        plaintext = salt + plaintext;  // Prepend salt to the text
        return shiftText(plaintext, shift);
    }

    /**
     * shifts each character in the string based on the Caesar cipher logic,
     * handling both uppercase and lowercase letters
     * ignores non-alphabetic characters
     * @param text
     * @param shift
     * @return
     */
    private String shiftText(String text, int shift) {
        StringBuilder encrypted = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (character >= 'a' && character <= 'z') {
                char shifted = (char) (((character - 'a' + shift) % 26) + 'a');
                encrypted.append(shifted);
            } else if (character >= 'A' && character <= 'Z') {
                char shifted = (char) (((character - 'A' + shift) % 26) + 'A');
                encrypted.append(shifted);
            } else {
                encrypted.append(character);
            }
        }
        return encrypted.toString();
    }

    public static void main(String[] args) {
        CaesarCipher cipher = new CaesarCipher(3); // Shift by 3
        String plaintext = "HelloWorld";

        String encryptedWithSalt = cipher.encrypt(plaintext, true);
        String encryptedWithoutSalt = cipher.encrypt(plaintext, false);

        System.out.println("Encrypted with salt: " + encryptedWithSalt);
        System.out.println("Encrypted without salt: " + encryptedWithoutSalt);
    }
}
