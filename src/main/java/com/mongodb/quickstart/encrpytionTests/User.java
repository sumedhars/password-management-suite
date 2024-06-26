package com.mongodb.quickstart.encrpytionTests;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class User {

    String rockYouPath = "C:\\Users\\sanjeevs\\IdeaProjects\\cpe4800-final-project\\src\\main\\java\\com\\mongodb\\quickstart\\encrpytionTests\\wordLists\\rockyou-15.txt";
    String dictionaryPath = "C:\\Users\\sanjeevs\\IdeaProjects\\cpe4800-final-project\\src\\main\\java\\com\\mongodb\\quickstart\\encrpytionTests\\wordLists\\dictionary.txt";

    // 10 passwords that are in common password database
    private ArrayList<HashMap<String, String>> commonPasswords;
    // 10 passwords that are random english words
    private ArrayList<HashMap<String, String>> englishWordPasswords;
    // 10 passwords that are randomly generated
    private ArrayList<HashMap<String, String>> randomPasswords;

    public User(){
        commonPasswords = generateCommonPasswords();
        englishWordPasswords = generateEnglishWordPasswords();
        randomPasswords = generateRandomPasswords();
    }

    public ArrayList<HashMap<String, String>> getCommonPasswords(){return this.commonPasswords;}

    public ArrayList<HashMap<String, String>> getEnglishWordPasswords(){
        return this.englishWordPasswords;
    }

    public ArrayList<HashMap<String, String>> getRandomPasswords(){
        return this.randomPasswords;
    }

    public void setCommonPasswords(ArrayList<HashMap<String, String>> commonPasswords){
        this.commonPasswords = commonPasswords;
    }

    public void setEnglishWordPasswords(ArrayList<HashMap<String, String>> englishWordPasswords){
        this.englishWordPasswords = englishWordPasswords;
    }

    public void setRandomPasswords(ArrayList<HashMap<String, String>> randomPasswords){
        this.randomPasswords = randomPasswords;
    }

    private ArrayList<HashMap<String, String>> generateCommonPasswords(){
        try {
            List<String> rockYou = Files.readAllLines(Paths.get(rockYouPath));
            Collections.shuffle(rockYou, new Random());
            List<String> selectedWords = rockYou.subList(0, 10);
            ArrayList<HashMap<String, String>> rockYouPasswords = new ArrayList<>();
            for (int i = 0; i < 10; i++){
                String password = selectedWords.get(i);
                HashMap<String, String> passwordMap = new HashMap<>();
                passwordMap.put("plaintextPwd", password);
                rockYouPasswords.add(passwordMap);
            }
            return rockYouPasswords;
        } catch (Exception e){
            System.out.println("Error reading rockYou.txt");
        }
        return null;
    }

    private ArrayList<HashMap<String, String>> generateEnglishWordPasswords(){
        try {
            List<String> dictionary = Files.readAllLines(Paths.get(dictionaryPath));
            Collections.shuffle(dictionary, new Random());
            List<String> selectedWords = dictionary.subList(0, 20);
            ArrayList<HashMap<String, String>> dictionaryPasswords = new ArrayList<>();
            for (int i = 11; i < 21; i++){
                // combine two words from dictionary
                int num = i - 11;
                String password = selectedWords.get(num) + selectedWords.get(num+10);
                HashMap<String, String> passwordMap = new HashMap<>();
                passwordMap.put("plaintextPwd", password);
                dictionaryPasswords.add(passwordMap);
            }
            return dictionaryPasswords;
        } catch (Exception e){
            //e.printStackTrace();
            System.out.println("Error reading dictionary.txt");
        }
        return null;
    }


    private ArrayList<HashMap<String, String>> generateRandomPasswords(){
        ArrayList<HashMap<String, String>> randomPasswords = new ArrayList<>();
        for (int i = 21; i < 31; i++){
            int randomLength = new Random().nextInt(5) + 8; // generate random length b/w 8-12
            String password = generateRandomString(randomLength);
            HashMap<String, String> passwordMap = new HashMap<>();
            passwordMap.put("plaintextPwd",password);
            randomPasswords.add(passwordMap);
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
