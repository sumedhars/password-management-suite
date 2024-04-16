package com.mongodb.quickstart.encrpytionTests;

public class TestsMain {

    public static void main(String[] args) {
        User user1 = new User();
        System.out.println("Common passwords: " + user1.getCommonPasswords());
        System.out.println("English word passwords: " + user1.getEnglishWordPasswords());
        System.out.println("Random passwords" + user1.getRandomPasswords());
    }

}
