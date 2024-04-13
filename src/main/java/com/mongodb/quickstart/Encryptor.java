package com.mongodb.quickstart;

public interface Encryptor {

    String encrypt(String plaintext);

    String decrypt(String ciphertext);
}
