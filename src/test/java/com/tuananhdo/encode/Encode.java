package com.tuananhdo.encode;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Encode {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String name = "slodierqwe";
        String pw = encoder.encode(name);
        System.out.println(pw);
    }
}
