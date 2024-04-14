package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Handling it from the perspective of a enterprise security officer.
 * notes:
 * 1. only admin gets to change the encryption algorithm.
 * 2. when changing encryption algorithm:
 *   a) it is changed for a user, one user at a time
 *   b) all the passwords stored are converted to that encrpytion alg.
 *   c) the encrpytion algorithm is updated in the appUsers collection.
 */


public class Admin {

    // TODO:
    //  - handle incorrect admin login attempt

    public static void main(String plaintextPassword) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)){

            Scanner scanner = new Scanner(System.in);

            MongoDatabase appUsers = mongoClient.getDatabase("appUsers");
            MongoDatabase pwdManager = mongoClient.getDatabase("pwd-manager");
            MongoCollection<Document> adminCollection = appUsers.getCollection("admin");

            Document adminDocument = adminCollection.find(new Document("user", "adminAccount")).first();
            String encryptedPassword = adminDocument.getString("pwd");

            if (verifyAdminLogin(plaintextPassword, encryptedPassword)){
                System.out.println("OPTIONS:");
                System.out.println("1. Change encryption algorithm for a user");
                Integer option1 = Integer.parseInt(scanner.nextLine());

                switch (option1) {
                    case 1 -> {
                        MongoCollection<Document> appUsersCollection = appUsers.getCollection("appUsers");
                        System.out.println("a) Enter 2PWD username to change encryption algorithm for: ");
                        String username = scanner.nextLine();
                        System.out.println("b) Enter 1 if desired algorithm is AES. Enter 2 if desired " +
                                "algorithm is TripleDES.");
                        Integer option2 = Integer.parseInt(scanner.nextLine());
                        switch (option2){
                            case 1 -> {
                                updateUserDocumentAlgorithm(appUsersCollection, username, "aes");
                                MongoCollection<Document> userPasswordCollection = pwdManager.getCollection(username);
                                changePasswordEncryption(userPasswordCollection, "aes");
                            }
                            case 2 -> {
                                updateUserDocumentAlgorithm(appUsersCollection, username, "3des");
                                MongoCollection<Document> userPasswordCollection = pwdManager.getCollection(username);
                                changePasswordEncryption(userPasswordCollection, "3des");
                            }
                            default -> System.out.println("Incorrect Option.");
                        }
                    }
                }
            }
        }
    }

    /**
     * updates the 'encryption' key in the appUsers collection for the username's document
     * @param collection that contains the user's encrypted passwords
     * @param username of password manager account
     * @param encryptionType 'aes' or '3des'
     */
    public static void updateUserDocumentAlgorithm(MongoCollection<Document> collection, String username,
                                                       String encryptionType){
        Document updateEncryption = new Document("$set", new Document("encryption", encryptionType));
        collection.updateOne(new Document("pwdMngrUsername", username), updateEncryption);
    }

    /**
     * update the encrypted passwords for all documents in a user collection
     * based on the desired encryption algorithm.
     * @param collection the password manager username's collection
     * @param desiredEncryptionType aes or 3des
     */
    public static void changePasswordEncryption(MongoCollection<Document> collection,
                                                String desiredEncryptionType){
        if (desiredEncryptionType.equals("aes")){
            // convert all passwords from 3DES (i.e. to decrypt) to AES (i.e. to encrypt)
            AESEncryptor aesEncryptor = new AESEncryptor();
            List<Document> passwordsList = collection.find().into(new ArrayList<>());
            for (Document document: passwordsList){
                //System.out.println(document);
                String encryption = document.getString("encryption");
                if (encryption.equals("3des")) {
                    TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor(TripleDESEncryptor.
                            stringToSecretKey(document.getString("key")));
                    String tripleDESCiphertext = document.getString("password");
                    //System.out.println(tripleDESCiphertext);
                    String plaintext = tripleDESEncryptor.decrypt(tripleDESCiphertext);
                    String aesCiphertext = aesEncryptor.encrypt(plaintext);
                    String key = AESEncryptor.toString(aesEncryptor.getSecretKey());
                    Document updatePassword = new Document("$set", new Document("password", aesCiphertext));
                    collection.updateOne(new Document("_id", document.get("_id")), updatePassword);
                    Document updateKey = new Document("$set", new Document("key", key));
                    collection.updateOne(new Document("_id", document.get("_id")), updateKey);
                    Document updateEncryptionType = new Document("$set", new Document("encryption", "aes"));
                    collection.updateOne(new Document("_id", document.get("_id")), updateEncryptionType);
                }
            }
        } else {
            // convert all passwords from AES (i.e. to decrypt) to 3DES (i.e. to encrypt)
            TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor();
            List<Document> passwordsList = collection.find().into(new ArrayList<>());
            for (Document document: passwordsList){
                String encryption = document.getString("encryption");
                if (encryption.equals("aes")) {
                    AESEncryptor aesEncryptor = new AESEncryptor(AESEncryptor.stringToSecretKey(document.getString("key")));
                    String aesCiphertext = document.getString("password");
                    String plaintext = aesEncryptor.decrypt(aesCiphertext);
                    String tripleDESCiphertext = tripleDESEncryptor.encrypt(plaintext);
                    String key = TripleDESEncryptor.toString(tripleDESEncryptor.getSecretKey());
                    Document updatePassword = new Document("$set", new Document("password", tripleDESCiphertext));
                    collection.updateOne(new Document("_id", document.get("_id")), updatePassword);
                    Document updateKey = new Document("$set", new Document("key", key));
                    collection.updateOne(new Document("_id", document.get("_id")), updateKey);
                    Document updateEncryptionType = new Document("$set", new Document("encryption", "3des"));
                    collection.updateOne(new Document("_id", document.get("_id")), updateEncryptionType);
                }
            }
        }
    }


    public static boolean verifyAdminLogin(String plaintextPassword, String encryptedPassword){
        boolean validLogin = false;
        Argon2Encryptor encryptor = new Argon2Encryptor();
        if (encryptor.verifyPassword(plaintextPassword, encryptedPassword)) {
            validLogin = true;
        }
        return validLogin;
    }

}
