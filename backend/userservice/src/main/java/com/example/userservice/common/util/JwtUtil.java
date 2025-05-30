package com.example.userservice.common.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.userservice.api.user.entity.UserRole;
import com.example.userservice.common.config.securiry.model.SecurityUser;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    private static SecretKey secretKey;
    private static JwtParser jwtParser;

    @Value("${jwt.secret-key}")
    private String originSecretKey;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @PostConstruct
    public void init() {
        log.debug("JwtUtil init, originSecretKey: {}", originSecretKey);
        JwtUtil.secretKey = new SecretKeySpec(originSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        JwtUtil.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    /**
     * JWT 생성
     */
    public static String generateToken(SecurityUser user, long second) {
        Instant now = Instant.now();
        Date nowDate = Date.from(now);
        Date expirationDate = Date.from(now.plusSeconds(second));

        return Jwts.builder()
                .claim("id", user.id().toString())
                .claim("username", user.getUsername())
                .claim("role", user.role().name())
                .issuedAt(nowDate)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성 (껍데기, UUID만 포함)
     */
    public static String generateRefreshToken(long second) {
        Instant now = Instant.now();
        Date nowDate = Date.from(now);
        Date expirationDate = Date.from(now.plusSeconds(second));

        return Jwts.builder()
                .subject(UUID.randomUUID().toString()) // 랜덤 UUID
                .issuedAt(nowDate)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public static Optional<String> getJwtFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(bearerToken -> bearerToken.startsWith(BEARER_PREFIX))
                .map(bearerToken -> bearerToken.substring(7));
    }

    /**
     * JWT 검증
     */
    public static boolean validateToken(String token) {
        try{
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT에서 사용자 정보 추출
     */
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

    public static UserRole getUserRole(String token) {
        return UserRole.valueOf(
                jwtParser
                        .parseSignedClaims(token)
                        .getPayload()
                        .get("role", String.class));
    }

    public static Date getExpiration(String token) {
        return jwtParser.parseSignedClaims(token)
                .getPayload().getExpiration();
    }

    public static JwtUserInfo getUserInfo(String token) {
        UUID id = getUserId(token);
        String username = getUsername(token);
        UserRole role = getUserRole(token);
        return new JwtUserInfo(id, username, role);
    }

    public record JwtUserInfo(
        UUID id,
        String username,
        UserRole role
    ) {}

}