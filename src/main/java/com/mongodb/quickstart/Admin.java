package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Updates.set;

/**
 * notes:
 * 1. only admin gets to change the encryption algorithm.
 * 2. when changing encryption algorithm:
 *   a) it is changed for a user, one user at a time
 *   b) all the passwords stored are converted to that encrpytion alg.
 *   c) the encrpytion algorithm is updated in the appUsers collection.
 */

public class Admin {

    public static void main(String plaintextPassword) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)){

            Scanner scanner = new Scanner(System.in);

            MongoDatabase appUsers = mongoClient.getDatabase("appUsers");
            MongoCollection<Document> adminCollection = appUsers.getCollection("admin");

            Document adminDocument = adminCollection.find(new Document("user", "adminAccount")).first();
            String encryptedPassword = adminDocument.getString("pwd");

            if (verifyAdminLogin(plaintextPassword, encryptedPassword)){
                System.out.println("OPTIONS:");
                System.out.println("1. Change encryption algorithm");
                Integer option1 = Integer.parseInt(scanner.nextLine());

                switch (option1) {
                    case 1 -> {
                        MongoCollection<Document> usersCollection = appUsers.getCollection("appUsers");
                        System.out.println("-----x------");
                        System.out.println("Options:");
                        System.out.println("1. AES Encryption");
                        System.out.println("2. 3DES Encryption");
                        Integer option2 = Integer.parseInt(scanner.nextLine());
                        switch (option2){
                            case 1 -> {
                                updateEncryptionAlgorithm(usersCollection, "aes");
                            }
                            case 2 -> {
                                updateEncryptionAlgorithm(usersCollection, "3des");
                            }
                            default -> System.out.println("Incorrect Option.");
                        }
                    }
                }
            }
        }
    }

    public static void updateEncryptionAlgorithm(MongoCollection<Document> collection, String encryptionType){
        List<Document> allUsersList = collection.find().into(new ArrayList<>()); // retrieve all documents
        for (Document user : allUsersList) {
            Object id = user.get("_id"); // get doc id
            Bson filter = new Document("_id", id); // create filter by id
            //Bson updateOperation = new Document("$set", new Document("encryptionAlgorithm", encryptionType));
            Bson updateOperation = set("alg", encryptionType);
            collection.updateOne(filter, updateOperation);
        }
    }

    public static void changePasswordEncryption(MongoCollection<Document> collection,
                                                String currentEncryptionType){
        //TODO
        if (currentEncryptionType.equals("aes")){

        } else {

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
