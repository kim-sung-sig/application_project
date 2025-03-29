package com.example.chatservice.common.properties;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static JwtDecoder jwtDecoder;

    @Value("${jwt.secret-key}")
    private String originSecretKey;

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            originSecretKey.getBytes(StandardCharsets.UTF_8),
            MacAlgorithm.HS256.name() // "HmacSHA256"
        );
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
        JwtTokenProvider.jwtDecoder = decoder;
    }

    public static boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static UUID getUserId(String token) {
        return UUID.fromString((String) jwtDecoder.decode(token).getClaim("id"));
    }

    public static String getUsername(String token) {
        return (String) jwtDecoder.decode(token).getClaim("username");
    }

    public static List<String> getRoles(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("roles");
    }

}