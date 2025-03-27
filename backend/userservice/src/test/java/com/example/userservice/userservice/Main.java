package com.example.userservice.userservice;

import java.security.SecureRandom;
import java.util.Base64;

public class Main {

    public static String generateSecretKey(int keyLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretKey = new byte[keyLength / 8]; // 비밀키 길이 (비트 -> 바이트 변환)
        secureRandom.nextBytes(secretKey);
        return Base64.getEncoder().encodeToString(secretKey);
    }

    public static void main(String[] args) {
        // 256비트 비밀키 생성
        String secretKey = generateSecretKey(256);
        System.out.println("Generated Secret Key: " + secretKey);
    }

}
