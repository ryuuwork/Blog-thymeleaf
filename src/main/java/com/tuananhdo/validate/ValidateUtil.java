package com.tuananhdo.validate;

public class ValidateUtil {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 25;

    public static boolean isValidLengthPasswordAndEmptySpace(String password) {
        return password.trim().isEmpty() || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH;
    }
}
