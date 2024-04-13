package com.mongodb.quickstart;

import java.util.ArrayList;
import java.util.Scanner;

public class EntropyChecker {


    public static double calculateEntropy(String password) {
        int numPossibilities = countUniqueCharacters(password);
        int numCharacters = password.length();
        // calculate entropy using the formula: Entropy(bits) = log2((Number of possibilities)^(Number of characters))
        return Math.log(Math.pow(numPossibilities, numCharacters)) / Math.log(2);
    }


    private static int countUniqueCharacters(String str) {
        ArrayList<String> visited = new ArrayList<String>();
        for (int i = 0; i < str.length(); i++){
            if (!visited.contains(str.charAt(i) + "")) {
                String character = str.charAt(i) + "";
                visited.add(character);
            }
        }
        return visited.size();
    }


    // checking entropy checker implementation
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the password to check its entropy:");
        String password = scanner.nextLine();
        //System.out.println(countUniqueCharacters(password));
        double entropy = calculateEntropy(password);
        System.out.println("Entropy of the password: " + entropy + " bits");
    }

}
