package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.gte;

public class Main {

    //TODO:
    // - option for user to keep adding users + passwords until 'q'
    // - retrieve username + password for application after logging in
    // - handle incorrect user login

    //TODO: GUI!!

    //TODO: treat it from the perspective of the enterprise security officer:
    // - allow algorithms/rules/config to be changeable

    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            Scanner scanner = new Scanner(System.in);
            AESEncryptor aesEncryptor = new AESEncryptor();

            MongoDatabase appUsers = mongoClient.getDatabase("appUsers");
            MongoDatabase passwordManager = mongoClient.getDatabase("pwd-manager");
            MongoCollection<Document> appUsersCollection = appUsers.getCollection("appUsers");

            System.out.println("Database connection successful.");
            System.out.println("------------------------");
            System.out.println("WELCOME TO 2PWD Password Manager !!! \n");

            System.out.println("OPTIONS:");
            System.out.println("1. Current 2PWD user login");
            System.out.println("2. Create new 2PWD account");
            System.out.println("3: Admin mode");
            Integer option1 = Integer.parseInt(scanner.nextLine());

            boolean loginSuccess = false;
            String inputUsername = "";

            switch (option1) {

                case 1 -> {
                    System.out.println(" ----- USER LOGIN ----");
                    System.out.println("1. Enter your 2PWD username: ");
                    inputUsername = scanner.nextLine();
                    System.out.println("2. Enter your 2PWD password: ");
                    String inputPassword = scanner.nextLine();
                    loginSuccess = checkLoginSuccess(appUsersCollection, inputUsername, inputPassword);
                }

                case 2 -> {
                    System.out.println(" ----- CREATE ACCOUNT ----");
                    String inputPassword;
                    String confirmPassword;
                    boolean validUsername = false;
                    while (!validUsername) {
                        System.out.println("1. Enter your 2PWD username: ");
                        inputUsername = scanner.nextLine();
                        // check if the username already exists in the collection
                        Document existingUser = appUsersCollection.find(new Document("pwdMngrUsername", inputUsername)).first();
                        if (existingUser == null) {
                            validUsername = true;
                        } else {
                            System.out.println("Username '" + inputUsername + "' already exists. Please choose a different username.");
                        }
                    }
                    System.out.println("2. Enter your 2PWD password: ");
                    inputPassword = scanner.nextLine();
                    System.out.println("3. Re-enter your 2PWD Password: ");
                    confirmPassword = scanner.nextLine();
                    if (confirmPassword.equals(inputPassword)){
                        createUserAccount(appUsersCollection, inputUsername, inputPassword);
                        passwordManager.createCollection(inputUsername);
                        System.out.println("Collection '" + inputUsername + "' created successfully in the pwd-manager database.");
                        loginSuccess = true;
                    }
                }

                default -> System.out.println("Invalid choice.");
            }

            if (loginSuccess) {
                MongoCollection<Document> userCollection = passwordManager.getCollection(inputUsername);

                System.out.println("If you would like to enter data, please enter 1.");
                System.out.println("If you would like to retrieve data, please enter 2.");
                Integer option2 = Integer.parseInt(scanner.nextLine());

                switch (option2) {

                    case 1 -> {
                        System.out.println("1. Enter the application/website name ");
                        String applicationName = scanner.nextLine();
                        System.out.println("2. Enter the application username: ");
                        String username = scanner.nextLine();
                        System.out.println("3. Enter the application password: ");
                        String password = scanner.nextLine();

                        double passwordEntropy = EntropyChecker.calculateEntropy(password);
                        System.out.println("Password Entropy: " + passwordEntropy);

                        String cipherPassword = aesEncryptor.encrypt(password);
                        String key = AESEncryptor.toString(aesEncryptor.getSecretKey());
                        insertUsernamePassword(userCollection, applicationName, username, cipherPassword, key);
                    }

                    case 2 -> {
                        SecretKeySpec secretKey = aesEncryptor.getSecretKey();
                        System.out.println("1. Enter the application/website name ");
                        String applicationName = scanner.nextLine();
                        ArrayList<HashMap<String, String>> dataList =
                                retrieveUsernamePassword(userCollection, applicationName, secretKey);
                        if (dataList.isEmpty()){
                            System.out.println("No records found.");
                        } else {
                            System.out.println("Records found: ");
                            System.out.println(dataList);
                        }
                    }

                    default -> System.out.println("Invalid choice.");
                }

            }
        }
    }


    private static boolean checkLoginSuccess(MongoCollection<Document> collection, String username,
                                     String password){
        boolean loginSuccess = false;
        // search for the user in the database
        Document query = new Document("pwdMngrUsername", username);
        Document userFound = collection.find(query).first();
        // check if user exists and password matches
        if (userFound != null && userFound.getString("pwd").equals(password)) {
            System.out.println("Authentication successful. Welcome, " + username + "!");
            loginSuccess = true;
        } else {
            System.out.println("Authentication failed. Invalid username or password.");
        }
        return loginSuccess;
    }


    private static void createUserAccount(MongoCollection<Document> collection, String username, String password){
        // create a new document with the username and password
        Document newUser = new Document("pwdMngrUsername", username)
                .append("pwd", password);
        // insert the document into the collection
        collection.insertOne(newUser);
        System.out.println("User account created successfully for " + username + ".");
    }


    //TODO
    // - update to take in plaintext password + password type
    // -
    private static void insertUsernamePassword(MongoCollection<Document> collection, String appName,
                                               String username, String encryptedPassword, String key) {
        Document usernamePasswordDoc = new Document("appName", appName).
                append("username", username).append("password", encryptedPassword).append("key", key);
        collection.insertOne(usernamePasswordDoc);
        System.out.println("Username and password inserted successfully.");
    }


    private static ArrayList<HashMap<String, String>> retrieveUsernamePassword(
            MongoCollection<Document> collection, String appName, SecretKeySpec secretKey){
        // find a list of documents and use a List object instead of an iterator
        List<Document> documentList = collection.find(gte("appName", appName)).into(new ArrayList<>());
        System.out.println(documentList);
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        for (Document document : documentList) {
            HashMap<String, String> appUsernamePwd= new HashMap<>();
            appUsernamePwd.put("appName", document.getString("appName"));
            appUsernamePwd.put("username", document.getString("username"));
            String ciphertextPassword = document.getString("password");
            //System.out.println(document.getString("key"));
            AESEncryptor aesEncryptor = new AESEncryptor(AESEncryptor.stringToSecretKey(document.getString("key")));
            String plaintext = aesEncryptor.decrypt(ciphertextPassword);
            appUsernamePwd.put("password", plaintext);
            dataList.add(appUsernamePwd);
        }
        return dataList;
    }

}
