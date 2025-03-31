package com.example.chatservice.common.properties;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private static SecretKey secretKey;
    private static JwtParser jwtParser;

    @Value("${jwt.secret-key}")
    private String originSecretKey;

    @PostConstruct
    public void init() {
        log.debug("JwtTokenProvider init, originSecretKey: {}", originSecretKey);
        JwtTokenProvider.secretKey = new SecretKeySpec(originSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        JwtTokenProvider.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public static boolean validateToken(String token) {
        try{
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static UUID getUserId(String token) {
        return UUID.fromString(jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class));
    }

    public static String getUsername(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public static List<String> getRoles(String token) {
        String role = jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        return Collections.singletonList(role);
    }

}