package com.example.chatservice.common.config.security;

import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.chatservice.common.properties.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        // String token = authentication.getCredentials().toString();

        if (JwtTokenProvider.validateToken(token)) {
            UUID userId = JwtTokenProvider.getUserId(token);
            String username = JwtTokenProvider.getUsername(token);
            List<String> roles = JwtTokenProvider.getRoles(token);
            CustomUserDetails userDetails = new CustomUserDetails(userId, username, roles);
            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()));
        }

        return Mono.empty();
    }

}
