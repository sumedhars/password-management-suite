package com.mongodb.quickstart.encrpytionTests;

import com.mongodb.quickstart.AESEncryptor;
import com.mongodb.quickstart.CaesarCipher;
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
        passwordMap.put("encryption", "aes");
    }


    public static void encryptPasswords3DES(User user) {
        // Get password lists
        ArrayList<HashMap<String, String>> commonPasswords = user.getCommonPasswords();
        ArrayList<HashMap<String, String>> englishWordPasswords = user.getEnglishWordPasswords();
        ArrayList<HashMap<String, String>> randomPasswords = user.getRandomPasswords();
        // AES encryptor instance
        TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor();
        // Encrypt the fourth, fifth, and sixth entries of each password list
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
        passwordMap.put("encryption", "3des");
    }


    /**
     * Encrypts passwords with the Caesar cipher for the seventh, eighth, and ninth entries of all three lists
     * @param user
     */
    public static void encryptPasswordsCaesar(User user) {
        // Get password lists
        ArrayList<HashMap<String, String>> commonPasswords = user.getCommonPasswords();
        ArrayList<HashMap<String, String>> englishWordPasswords = user.getEnglishWordPasswords();
        ArrayList<HashMap<String, String>> randomPasswords = user.getRandomPasswords();
        // Instance of CaesarCipher
        CaesarCipher caesarCipher = new CaesarCipher(5); // Shift by 5, or any other desired value

        // Encrypt the seventh, eighth, and ninth entries of each password list (index 6 to 8)
        for (int i = 6; i < 8; i++) {
            encryptPasswordEntryCaesarWithSalt(commonPasswords, i, caesarCipher);
            encryptPasswordEntryCaesarWithSalt(englishWordPasswords, i, caesarCipher);
            encryptPasswordEntryCaesarWithSalt(randomPasswords, i, caesarCipher);
        }
        for (int i = 8; i < 10; i++) {
            encryptPasswordEntryCaesarWithoutSalt(commonPasswords, i, caesarCipher);
            encryptPasswordEntryCaesarWithoutSalt(englishWordPasswords, i, caesarCipher);
            encryptPasswordEntryCaesarWithoutSalt(randomPasswords, i, caesarCipher);
        }
        // update the user's password lists
        user.setCommonPasswords(commonPasswords);
        user.setEnglishWordPasswords(englishWordPasswords);
        user.setRandomPasswords(randomPasswords);
    }

    private static void encryptPasswordEntryCaesarWithSalt(ArrayList<HashMap<String, String>> passwordList, int index,
                                                   CaesarCipher caesarCipher) {
        HashMap<String, String> passwordMap = passwordList.get(index);
        String plaintext = passwordMap.get("plaintextPwd");
        String ciphertext = caesarCipher.encrypt(plaintext, true);
        // Update the map with the encrypted password
        passwordMap.put("cipherPwd", ciphertext);
        passwordMap.put("encryption", "caesarWithSalt");
    }

    private static void encryptPasswordEntryCaesarWithoutSalt(ArrayList<HashMap<String, String>> passwordList, int index,
                                                           CaesarCipher caesarCipher) {
        HashMap<String, String> passwordMap = passwordList.get(index);
        String plaintext = passwordMap.get("plaintextPwd");
        String ciphertext = caesarCipher.encrypt(plaintext, false);
        // Update the map with the encrypted password
        passwordMap.put("cipherPwd", ciphertext);
        passwordMap.put("encryption", "caesarWithoutSalt");
    }

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
