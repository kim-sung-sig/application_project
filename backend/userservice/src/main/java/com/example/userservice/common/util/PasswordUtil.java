package com.example.userservice.common.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PasswordUtil {

    private static PasswordEncoder passwordEncoder;

    public PasswordUtil(PasswordEncoder passwordEncoder) {
        log.debug("PasswordUtil created");
        PasswordUtil.passwordEncoder = passwordEncoder;
        log.debug("PasswordUtil.passwordEncoder: {}", PasswordUtil.passwordEncoder);
    }

    @PostConstruct
    public void init() {
        log.debug("PasswordUtil initialized");
        if (passwordEncoder == null) {
            throw new RuntimeException("passwordEncoder is null");
        }
    }

    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
