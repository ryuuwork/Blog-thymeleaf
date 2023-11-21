package com.tuananhdo.encode;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class EncodePasswordTests {
    @Test
    public void testEncodedPassword() {
        String rawPassword = "adminadmin";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(rawPassword);
        System.out.println(encode);
    }

}
