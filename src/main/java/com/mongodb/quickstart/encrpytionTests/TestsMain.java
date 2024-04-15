package com.mongodb.quickstart.encrpytionTests;

public class TestsMain {

    public static void main(String[] args) {
        User user1 = new User();
        System.out.println(user1.getCommonPasswords());
        System.out.println(user1.getEnglishWordPasswords());
        System.out.println(user1.getRandomPasswords());
    }

}
