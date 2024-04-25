package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.gte;

public class Main {

    //TODO:
    // - handle incorrect user login
    // - update password for application functionality!!!

    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            Scanner scanner = new Scanner(System.in);

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
                    break;
                }
                case 2 -> {
                    System.out.println(" ----- CREATE ACCOUNT ----");
                    String inputPassword;
                    String confirmPassword;
                    boolean validUsername = false;
                    while (!validUsername) {
                        System.out.println("1. Enter your 2PWD username: ");
                        inputUsername = scanner.nextLine();
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
                    if (confirmPassword.equals(inputPassword)) {
                        createUserAccount(appUsersCollection, inputUsername, inputPassword);
                        passwordManager.createCollection(inputUsername);
                        System.out.println("Collection '" + inputUsername + "' created successfully in the pwd-manager database.");
                        loginSuccess = true;
                    }
                    break;
                }
                case 3 -> {
                    System.out.println(" ----- ADMIN MODE ----");
                    System.out.println("Enter the admin password: ");
                    String enteredAdminPassword = scanner.nextLine();
                    Admin.main(enteredAdminPassword);
                    break;
                }
                default -> {
                    System.out.println("Invalid choice.");
                    break;
                }
            }

            if (loginSuccess) {
                MongoCollection<Document> userCollection = passwordManager.getCollection(inputUsername);
                String encryptionType = appUsersCollection.find(new Document("pwdMngrUsername", inputUsername)).first()
                        .get("encryption").toString();

                boolean continueInput = true;
                while (continueInput) {
                    System.out.println("If you would like to enter data, please enter 1.");
                    System.out.println("If you would like to retrieve data, please enter 2.");
                    System.out.println("Enter 'q' to quit.");
                    String option2 = scanner.nextLine();

                    if (option2.equals("q")) {
                        continueInput = false;
                        break;
                    }

                    switch (option2) {
                        case "1" -> {
                            System.out.println("1. Enter the application/website name ");
                            String applicationName = scanner.nextLine();
                            System.out.println("2. Enter the application username: ");
                            String username = scanner.nextLine();
                            System.out.println("3. Enter the application password: ");
                            String password = scanner.nextLine();

                            double passwordEntropy = EntropyChecker.calculateEntropy(password);
                            System.out.println("Password Entropy: " + passwordEntropy);

                            if (encryptionType.equals("aes")) {
                                insertUsernamePasswordAES(userCollection, applicationName, username, password);
                            } else {
                                insertUsernamePassword3DES(userCollection, applicationName, username, password);
                            }
                            break;
                        }
                        case "2" -> {
                            System.out.println("1. Enter the application/website name ");
                            String applicationName = scanner.nextLine();
                            ArrayList<HashMap<String, String>> dataList = null;
                            if (encryptionType.equals("aes")) {
                                dataList = retrieveUsernamePasswordAES(userCollection, applicationName);
                            } else {
                                dataList = retrieveUsernamePassword3DES(userCollection, applicationName);
                            }
                            if (dataList.isEmpty()) {
                                System.out.println("No records found.");
                            } else {
                                System.out.println("Records found: ");
                                System.out.println(dataList);
                            }
                            break;
                        }
                        default -> {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    }
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
        if (userFound != null){
            String encryptedPassword = userFound.getString("pwd");
            if (verifyUserPassword(password, encryptedPassword)) {
                System.out.println("Authentication successful. Welcome, " + username + "!");
                loginSuccess = true;
            }
        } else {
            System.out.println("Authentication failed. Invalid username or password.");
        }
        return loginSuccess;
    }

    public static boolean verifyUserPassword(String plaintextPassword, String encryptedPassword){
        boolean validLogin = false;
        Argon2Encryptor encryptor = new Argon2Encryptor();
        if (encryptor.verifyPassword(plaintextPassword, encryptedPassword)) {
            validLogin = true;
        }
        return validLogin;
    }


    private static void createUserAccount(MongoCollection<Document> collection, String username, String password){
        // create a new document with the username and password
        // default encryption -> AES since it is stronger
        // user login password stored using argon2 encryptor
        Argon2Encryptor argon2Encryptor = new Argon2Encryptor();
        String encryptedUserPwd = argon2Encryptor.encrypt(password);
        Document newUser = new Document("pwdMngrUsername", username)
                .append("pwd", encryptedUserPwd).append("encryption", "aes");
        // insert the document into the collection
        collection.insertOne(newUser);
        System.out.println("User account created successfully for " + username + ".");
    }

    private static void insertUsernamePasswordAES(MongoCollection<Document> collection, String appName,
                                               String username, String plaintextPassword) {
        AESEncryptor aesEncryptor = new AESEncryptor();
        String encryptedPassword = aesEncryptor.encrypt(plaintextPassword);
        String key = AESEncryptor.toString(aesEncryptor.getSecretKey());
        Document usernamePasswordDoc = new Document("appName", appName).
                append("username", username).append("password", encryptedPassword).append("key", key).
                append("encryption", "aes");
        collection.insertOne(usernamePasswordDoc);
        System.out.println("Username and password inserted successfully.");
    }

    private static void insertUsernamePassword3DES(MongoCollection<Document> collection, String appName,
                                                  String username, String plaintextPassword) {
        TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor();
        String encryptedPassword = tripleDESEncryptor.encrypt(plaintextPassword);
        String key = TripleDESEncryptor.toString(tripleDESEncryptor.getSecretKey());
        Document usernamePasswordDoc = new Document("appName", appName).
                append("username", username).append("password", encryptedPassword).append("key", key).
                append("encryption", "3des");
        collection.insertOne(usernamePasswordDoc);
        System.out.println("Username and password inserted successfully.");
    }


    private static ArrayList<HashMap<String, String>> retrieveUsernamePasswordAES(
            MongoCollection<Document> collection, String appName){
        // find a list of documents and use a List object instead of an iterator
        List<Document> documentList = collection.find(gte("appName", appName)).into(new ArrayList<>());
        //System.out.println(documentList);
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


    private static ArrayList<HashMap<String, String>> retrieveUsernamePassword3DES(
            MongoCollection<Document> collection, String appName){
        List<Document> documentList = collection.find(gte("appName", appName)).into(new ArrayList<>());
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        for (Document document : documentList) {
            HashMap<String, String> appUsernamePwd= new HashMap<>();
            appUsernamePwd.put("appName", document.getString("appName"));
            appUsernamePwd.put("username", document.getString("username"));
            String ciphertextPassword = document.getString("password");
            //System.out.println(document.getString("key"));
            TripleDESEncryptor tripleDESEncryptor = new TripleDESEncryptor(TripleDESEncryptor.
                    stringToSecretKey(document.getString("key")));
            String plaintext = tripleDESEncryptor.decrypt(ciphertextPassword);
            appUsernamePwd.put("password", plaintext);
            dataList.add(appUsernamePwd);
        }
        return dataList;
    }

    private static void updateUsernamePasswordAES(){
        //TODO
    }

    private static void updateUsernamePassword3DES(){
        //TODO
    }

}
