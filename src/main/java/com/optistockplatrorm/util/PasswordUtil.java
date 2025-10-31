package com.optistockplatrorm.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public static String hash(String raw) {
        return encoder.encode(raw);
    }

    public static boolean verify(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }
}
