package com.example.chatservice.common.properties;

import java.time.Instant;
import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600); // 1시간 유효

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwtClaimsSet.builder()
                                .issuer("chat-app")
                                .issuedAt(now)
                                .expiresAt(expiry)
                                .subject(username)
                                .claim("roles", roles)
                                .build()))
                .getTokenValue();
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public List<String> getRoles(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("roles");
    }
}