package com.example.userservice.common.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.userservice.common.config.securiry.model.SecurityUser;
import com.example.userservice.common.util.JwtUtil;
import com.example.userservice.common.util.JwtUtil.JwtUserInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<String> token = JwtUtil.getJwtFromRequest(request);

        if (token.isPresent() && JwtUtil.validateToken(token.get())) {
            JwtUserInfo userInfo = JwtUtil.getUserInfo(token.get());

            SecurityUser securityUser = SecurityUser.of(userInfo);

            Authentication authToken = new UsernamePasswordAuthenticationToken(
                securityUser,                   // principal
                token.get(),                    // credentials
                securityUser.getAuthorities()   // authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}