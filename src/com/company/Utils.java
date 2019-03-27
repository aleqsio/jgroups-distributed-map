package com.company;

public class Utils {
    public static String getRandomWord(int length) {
        String r = "";
        for(int i = 0; i < length; i++) {
            r += (char)(Math.random() * 26 + 97);
        }
        return r;
    }
}
