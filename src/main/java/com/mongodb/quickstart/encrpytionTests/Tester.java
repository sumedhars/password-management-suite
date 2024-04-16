package com.mongodb.quickstart.encrpytionTests;

import com.mongodb.quickstart.AESEncryptor;
import com.mongodb.quickstart.TripleDESEncryptor;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * tests the performance + ability to break
 */
public class Tester {

    public static void encryptPasswordsAES(User user) {
        // Get password lists
        ArrayList<HashMap<String, String>> commonPasswords = user.getCommonPasswords();
        ArrayList<HashMap<String, String>> englishWordPasswords = user.getEnglishWordPasswords();
        ArrayList<HashMap<String, String>> randomPasswords = user.getRandomPasswords();
        // AES encryptor instance
        AESEncryptor aesEncryptor = new AESEncryptor();
        // Encrypt the first three entries of each password list
        for (int i = 0; i < 3; i++) {
            encryptPasswordEntryAES(commonPasswords, i, aesEncryptor);
            encryptPasswordEntryAES(englishWordPasswords, i, aesEncryptor);
            encryptPasswordEntryAES(randomPasswords, i, aesEncryptor);
        }
        // update the user's password lists
        user.setCommonPasswords(commonPasswords);
        user.setEnglishWordPasswords(englishWordPasswords);
        user.setRandomPasswords(randomPasswords);
    }


    /**
     * Helper method to encrypt a single password entry
     * @param passwordList
     * @param index
     * @param aesEncryptor
     */
    private static void encryptPasswordEntryAES(ArrayList<HashMap<String, String>> passwordList, int index,
                                                AESEncryptor aesEncryptor) {
        HashMap<String, String> passwordMap = passwordList.get(index);
        String plaintext = passwordMap.get("plaintextPwd");
        String ciphertext = aesEncryptor.encrypt(plaintext);
        String key = AESEncryptor.toString(aesEncryptor.getSecretKey());
        // Update the map with the encrypted password and key
        passwordMap.put("cipherPwd", ciphertext);
        passwordMap.put("key", key);
    }


    public static void encryptPasswords3DES(User user) {
        // Get password lists
        ArrayList<HashMap<String, String>> commonPasswords = user.getCommonPasswords();
        ArrayList<HashMap<String, String>> englishWordPasswords = user.getEnglishWordPasswords();
        ArrayList<HashMap<String, String>> randomPasswords = user.getRandomPasswords();
        // AES encryptor instance
        TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor();
        // Encrypt the first three entries of each password list
        for (int i = 3; i < 6; i++) {
            encryptPasswordEntry3DES(commonPasswords, i, tripleDESEncryptor);
            encryptPasswordEntry3DES(englishWordPasswords, i, tripleDESEncryptor);
            encryptPasswordEntry3DES(randomPasswords, i, tripleDESEncryptor);
        }
        // update the user's password lists
        user.setCommonPasswords(commonPasswords);
        user.setEnglishWordPasswords(englishWordPasswords);
        user.setRandomPasswords(randomPasswords);
    }


    /**
     * Helper method to encrypt a single password entry
     * @param passwordList
     * @param index
     * @param tripleDESEncryptor
     */
    private static void encryptPasswordEntry3DES(ArrayList<HashMap<String, String>> passwordList, int index,
                                                 TripleDESEncryptor tripleDESEncryptor) {
        HashMap<String, String> passwordMap = passwordList.get(index);
        String plaintext = passwordMap.get("plaintextPwd");
        String ciphertext = tripleDESEncryptor.encrypt(plaintext);
        String key = TripleDESEncryptor.toString(tripleDESEncryptor.getSecretKey());
        // Update the map with the encrypted password and key
        passwordMap.put("cipherPwd", ciphertext);
        passwordMap.put("key", key);
    }

    //TODO: stream cipher

    public static ArrayList<User> generateUsers(int numUsers){
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < numUsers; i++){
            User user = new User();
            users.add(user);
        }
        return users;
    }


    public static void main(String[] args) {
        User user = new User();
        System.out.println("Common passwords: " + user.getCommonPasswords());
        System.out.println("English word passwords: " + user.getEnglishWordPasswords());
        System.out.println("Random passwords: " + user.getRandomPasswords());
        Tester.encryptPasswordsAES(user);
        Tester.encryptPasswords3DES(user);
        System.out.println("Common passwords: " + user.getCommonPasswords());
        System.out.println("English word passwords: " + user.getEnglishWordPasswords());
        System.out.println("Random passwords: " + user.getRandomPasswords());
    }


}
