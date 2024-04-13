package com.mongodb.quickstart;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2Encryptor {

    public Argon2Encryptor() {}

    private String encrypt(String plaintext){
        Argon2 argon2 = Argon2Factory.create();
        try {
            String hashedPassword = hashPassword(argon2, plaintext);
            return hashedPassword;
        } finally {
            argon2.wipeArray(plaintext.toCharArray());
        }
    }

    private String hashPassword(Argon2 argon2, String password) {
        // Configure Argon2 parameters as needed
        int iterations = 2;
        int memory = 65536;
        int parallelism = 1;

        return argon2.hash(iterations, memory, parallelism, password);
    }

    protected boolean verifyPassword(String password, String hash) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.verify(hash, password);
    }

    public static void main(String[] args) {
        String password = "AdminPassword@63202";
        Argon2Encryptor encryptor = new Argon2Encryptor();
        String ciphertext = encryptor.encrypt(password);
        System.out.println(ciphertext);
        System.out.println(encryptor.verifyPassword(password, ciphertext));
    }
}