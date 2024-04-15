package com.mongodb.quickstart.encrpytionTests;

import org.bson.BsonElement;

import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class User {

    String rockYouPath = "C:\\Users\\sanjeevs\\IdeaProjects\\cpe4800-final-project\\src\\main\\java\\com\\mongodb\\quickstart\\encrpytionTests\\wordLists\\rockyou-15.txt";
    String dictionaryPath = "C:\\Users\\sanjeevs\\IdeaProjects\\cpe4800-final-project\\src\\main\\java\\com\\mongodb\\quickstart\\encrpytionTests\\wordLists\\dictionary.txt";

    // 10 passwords that are in common password database
    HashMap<String, String> commonPasswords;
    // 10 passwords that are random english words
    HashMap<String, String> englishWordPasswords;
    // 10 passwords that are randomly generated
    HashMap<String, String> randomPasswords;

    public User(){
        commonPasswords = generateCommonPasswords();
        englishWordPasswords = generateEnglishWordPasswords();
        randomPasswords = generateRandomPasswords();
    }

    public HashMap<String, String> getCommonPasswords(){
        return this.commonPasswords;
    }

    public HashMap<String, String> getEnglishWordPasswords(){
        return this.englishWordPasswords;
    }

    public HashMap<String, String> getRandomPasswords(){
        return this.randomPasswords;
    }

    private HashMap<String, String> generateCommonPasswords(){
        try {
            List<String> rockYou = Files.readAllLines(Paths.get(rockYouPath));
            Collections.shuffle(rockYou, new Random());
            List<String> selectedWords = rockYou.subList(0, 10);
            HashMap<String, String> rockYouPasswords = new HashMap<>();
            String baseUsername = "username";
            for (int i = 0; i < 10; i++){
                String username = baseUsername + (i+1);
                String password = selectedWords.get(i);
                rockYouPasswords.put(username, password);
            }
            return rockYouPasswords;
        } catch (Exception e){
            System.out.println("Error reading rockYou.txt");
        }
        return null;
    }

    private HashMap<String, String> generateEnglishWordPasswords(){
        try {
            List<String> dictionary = Files.readAllLines(Paths.get(dictionaryPath));
            Collections.shuffle(dictionary, new Random());
            List<String> selectedWords = dictionary.subList(0, 20);
            HashMap<String, String> dictionaryPasswords = new HashMap<>();
            String baseUsername = "username";
            for (int i = 11; i < 21; i++){
                String username = baseUsername + i;
                // combine two words from dictionary
                int num = i - 11;
                String password = selectedWords.get(num) + selectedWords.get(num+10);
                dictionaryPasswords.put(username, password);
            }
            return dictionaryPasswords;
        } catch (Exception e){
            //e.printStackTrace();
            System.out.println("Error reading dictionary.txt");
        }
        return null;
    }


    private HashMap<String, String> generateRandomPasswords(){
        HashMap<String, String> randomPasswords = new HashMap<>();
        String baseUsername = "username";
        for (int i = 21; i < 31; i++){
            int randomLength = new Random().nextInt(5) + 8; // generate random length b/w 8-12
            String password = generateRandomString(randomLength);
            String username = baseUsername + i;
            randomPasswords.put(username, password);
        }
        return randomPasswords;
    }


    /**
     * Method to generate a random alphanumeric string of a specific length
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}
