package com.mongodb.quickstart.encrpytionTests;

import java.util.*;

import joinery.DataFrame;

public class TestsMain {

    public static void main(String[] args) {

        ArrayList<User> userList = Tester.generateUsers(10);

        List<String> rows = Arrays.asList("aes", "3des", "caesarWithSalt", "caesarWithoutSalt");
        List<String> columns = Arrays.asList("commonPassword", "englishWordPassword", "randomPassword");
        DataFrame<Object> successfulBruteForce = new DataFrame<>(rows, columns);
        DataFrame<Object> unsuccessfulBruteForce = new DataFrame<>(rows, columns);
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                successfulBruteForce.set(i, j, 0);
                unsuccessfulBruteForce.set(i, j, 0);
            }
        }

        for (User user : userList) {
            ArrayList<HashMap<String, String>> passwords1 = user.getCommonPasswords();
            ArrayList<HashMap<String, String>> passwords2 = user.getEnglishWordPasswords();
            ArrayList<HashMap<String, String>> passwords3 = user.getRandomPasswords();

            int maxDecryptionAttempts = 5000;

            ArrayList<HashMap<String, String>> bruteForceLogs1 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords1);
            System.out.println(bruteForceLogs1);
            ArrayList<DataFrame> bruteForce1DfList = valueCounts(bruteForceLogs1, successfulBruteForce,
                    unsuccessfulBruteForce, "commonPassword");
            successfulBruteForce = bruteForce1DfList.get(0);
            unsuccessfulBruteForce = bruteForce1DfList.get(1);

            ArrayList<HashMap<String, String>> bruteForceLogs2 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords2);
            System.out.println(bruteForceLogs2);
            ArrayList<DataFrame> bruteForce2DfList = valueCounts(bruteForceLogs2, successfulBruteForce,
                    unsuccessfulBruteForce, "englishWordPassword");
            successfulBruteForce = bruteForce2DfList.get(0);
            unsuccessfulBruteForce = bruteForce2DfList.get(1);

            ArrayList<HashMap<String, String>> bruteForceLogs3 =
                    bruteForceDecrypt(user, maxDecryptionAttempts, passwords3);
            System.out.println(bruteForceLogs3);
            ArrayList<DataFrame> bruteForce3DfList = valueCounts(bruteForceLogs3, successfulBruteForce,
                    unsuccessfulBruteForce, "randomPassword");
            successfulBruteForce = bruteForce3DfList.get(0);
            unsuccessfulBruteForce = bruteForce3DfList.get(1);
        }
        System.out.println(successfulBruteForce);
        System.out.println(unsuccessfulBruteForce);
    }

    public static ArrayList<HashMap<String, String>> bruteForceDecrypt(User user, int maxDecryptionAttempts,
                                         ArrayList<HashMap<String, String>> passwords){
        ArrayList<HashMap<String, String>> bruteForceLog = new ArrayList<>();
        Tester.encryptPasswordsAES(user);
        Tester.encryptPasswords3DES(user);
        Tester.encryptPasswordsCaesar(user);
        BruteForceDecryptor bruteForceDecryptor = new BruteForceDecryptor();
        Random random = new Random();
        ArrayList<String> completedPasswords = new ArrayList<>();
        while (completedPasswords.size() != passwords.size() ){
            HashMap<String, String> randomPwdMap = passwords.get(random.nextInt(passwords.size()));
            String trialCiphertext = randomPwdMap.get("cipherPwd");
            String plaintext = randomPwdMap.get("plaintextPwd");
            if (!completedPasswords.contains(trialCiphertext)) {
                String trialEncryptionType = randomPwdMap.get("encryption");
                String bruteForceDecrypt;
                if (trialEncryptionType.equals("aes")) {
                    bruteForceDecrypt = bruteForceDecryptor.decryptAES(trialCiphertext,
                            maxDecryptionAttempts, plaintext);
                } else if (trialEncryptionType.equals("3des")){
                    bruteForceDecrypt = bruteForceDecryptor.decrypt3DES(trialCiphertext,
                            maxDecryptionAttempts, plaintext);
                } else {
                    bruteForceDecrypt = bruteForceDecryptor.decryptCaesar(trialCiphertext, plaintext);
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

    public static ArrayList<DataFrame> valueCounts(ArrayList<HashMap<String, String>> bruteForceLogs,
                                            DataFrame<Object> successfulBruteForce,
                                            DataFrame<Object> unsuccessfulBruteForce, String passwordType){
        ArrayList<DataFrame> toReturn = new ArrayList<>();
        for (HashMap<String, String> passwordMap: bruteForceLogs){
            String encryption = passwordMap.get("encryption");
            String bruteForceSuccess = passwordMap.get("bruteForceSuccess");
            if (encryption.equals("aes")){
                if (bruteForceSuccess.equals("true")){
                    int currentValue = (int) successfulBruteForce.get("aes", passwordType);
                    successfulBruteForce.set("aes", passwordType, currentValue + 1 );
                } else {
                    int currentValue = (int) unsuccessfulBruteForce.get("aes", passwordType);
                    unsuccessfulBruteForce.set("aes", passwordType, currentValue + 1 );
                }
            } else if (encryption.equals("3des")){
                if (bruteForceSuccess.equals("true")){
                    int currentValue = (int) successfulBruteForce.get("3des", passwordType);
                    successfulBruteForce.set("3des", passwordType, currentValue + 1 );
                } else {
                    int currentValue = (int) unsuccessfulBruteForce.get("3des", passwordType);
                    unsuccessfulBruteForce.set("3des", passwordType, currentValue + 1 );
                }
            } else if (encryption.equals("caesarWithoutSalt")){
                if (bruteForceSuccess.equals("true")){
                    int currentValue = (int) successfulBruteForce.get("caesarWithoutSalt", passwordType);
                    successfulBruteForce.set("caesarWithoutSalt", passwordType, currentValue + 1 );
                } else {
                    int currentValue = (int) unsuccessfulBruteForce.get("caesarWithoutSalt", passwordType);
                    unsuccessfulBruteForce.set("caesarWithoutSalt", passwordType, currentValue + 1 );
                }
            } else {
                if (bruteForceSuccess.equals("true")){
                    int currentValue = (int) successfulBruteForce.get("caesarWithSalt", passwordType);
                    successfulBruteForce.set("caesarWithSalt", passwordType, currentValue + 1 );
                } else {
                    int currentValue = (int) unsuccessfulBruteForce.get("caesarWithSalt", passwordType);
                    unsuccessfulBruteForce.set("caesarWithSalt", passwordType, currentValue + 1 );
                }
            }
        }
        toReturn.add(successfulBruteForce);
        toReturn.add(unsuccessfulBruteForce);
        return toReturn;
    }

}




//    ArrayList<HashMap<String, String>> commonPasswords = user1.getCommonPasswords();
//    List<String> ciphertexts = commonPasswords.stream()
//            .map(dataItem -> dataItem.get("cipherPwd"))
//            .collect(Collectors.toList());