package com.tuananhdo.encode;

public class Enviroment {

    public static void main(String[] args) {
        System.out.println(System.getenv("ENV_EMAIL_FILE"));
        System.out.println(replaceAllSpace(""));
    }
    public static String replaceAllSpace(String input){
        return input.replaceAll("\\s","");
    }
}
