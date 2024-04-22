package com.mongodb.quickstart.encrpytionTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TestsMain {

    public static void main(String[] args) {

        ArrayList<User> userList = Tester.generateUsers(10);
        for (User user : userList) {
            ArrayList<HashMap<String, String>> passwords1 = user.getCommonPasswords();
            ArrayList<HashMap<String, String>> passwords2 = user.getEnglishWordPasswords();
            ArrayList<HashMap<String, String>> passwords3 = user.getRandomPasswords();

            int maxDecryptionAttempts = 5000;

            ArrayList<HashMap<String, String>> bruteForceLogs1 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords1);
            System.out.println(bruteForceLogs1);

            ArrayList<HashMap<String, String>> bruteForceLogs2 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords2);
            System.out.println(bruteForceLogs2);

            ArrayList<HashMap<String, String>> bruteForceLogs3 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords3);
            System.out.println(bruteForceLogs3);
        }
    }

    public static ArrayList<HashMap<String, String>> bruteForceDecrypt(User user, int maxDecryptionAttempts,
                                         ArrayList<HashMap<String, String>> passwords){
        ArrayList<HashMap<String, String>> bruteForceLog = new ArrayList<>();
        Tester.encryptPasswordsAES(user);
        Tester.encryptPasswords3DES(user);
        BruteForceDecryptor bruteForceDecryptor = new BruteForceDecryptor();
        Random random = new Random();
        ArrayList<String> completedPasswords = new ArrayList<>();
        while (completedPasswords.size() != 6 ){
            HashMap<String, String> randomPwdMap = passwords.get(random.nextInt(6)); //TODO: change
            String trialCiphertext = randomPwdMap.get("cipherPwd");
            String plaintext = randomPwdMap.get("plaintextPwd");
            if (!completedPasswords.contains(trialCiphertext)) {
                String trialEncryptionType = randomPwdMap.get("encryption");
                String bruteForceDecrypt;
                if (trialEncryptionType.equals("aes")) {
                    bruteForceDecrypt = bruteForceDecryptor.decryptAES(trialCiphertext,
                            maxDecryptionAttempts, plaintext);
                } else {
                    bruteForceDecrypt = bruteForceDecryptor.decrypt3DES(trialCiphertext,
                            maxDecryptionAttempts, plaintext);
                }
                HashMap<String, String> log = new HashMap<>();
                log.put("cipherPwd", trialCiphertext);
                log.put("encryption", trialEncryptionType);
                if (bruteForceDecrypt == null) {
                    log.put("bruteForceSuccess", "false");
                } else {
                    log.put("bruteForceSuccess", "true");
                }
                completedPasswords.add(trialCiphertext);
                bruteForceLog.add(log);
            }
            // System.out.println(completedPasswords);
        }
        return bruteForceLog;
    }

}


//    ArrayList<HashMap<String, String>> commonPasswords = user1.getCommonPasswords();
//    List<String> ciphertexts = commonPasswords.stream()
//            .map(dataItem -> dataItem.get("cipherPwd"))
//            .collect(Collectors.toList());