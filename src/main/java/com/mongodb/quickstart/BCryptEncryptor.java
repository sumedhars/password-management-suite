package com.mongodb.quickstart;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptEncryptor implements Encrpytor{

    // represents the complexity of the hash computation
    private static final int WORKLOAD = 12;

    public BCryptEncryptor() {}

    @Override
    public String encrypt(String plaintext) {
        try {
            return BCrypt.hashpw(plaintext, BCrypt.gensalt(WORKLOAD));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  instead of a decrypt method, bcrypt checks if a
     *  given plaintext password corresponds to a stored hash because
     *  the hash cannot be reversed.
     * @param plaintext
     * @param hashed
     * @return boolean
     */
    public boolean checkPassword(String plaintext, String hashed) {
        try {
            return BCrypt.checkpw(plaintext, hashed);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        BCryptEncryptor encryptor = new BCryptEncryptor();
        String originalPassword = "securePassword123";
        // encrypt (hash) the password
        String hashedPassword = encryptor.encrypt(originalPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        // check pwd against hash
        boolean isCorrect = encryptor.checkPassword(originalPassword, hashedPassword);
        System.out.println("Password verification: " + isCorrect);
        // test w/ wrong pwd
        boolean isWrong = encryptor.checkPassword("wrongPassword123", hashedPassword);
        System.out.println("Wrong password verification: " + isWrong);
    }

}
