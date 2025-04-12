package com.example.userservice.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.auth.request.TokenRefreshRequest;
import com.example.userservice.api.auth.request.TokenValidateRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.AuthService;
import com.example.userservice.common.response.RsData;
import com.example.userservice.common.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/token")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    // 토큰 발급 with (refresh token)
    @PostMapping("/refresh")
    public ResponseEntity<RsData< JwtTokenResponse >> refreshToken(@Valid @RequestBody TokenRefreshRequest refreshToken) {
        log.debug("refresh request : {}", refreshToken);

        JwtTokenResponse response = authService.createTokenByRefreshToken(refreshToken.refreshToken());
        log.debug("refresh response : {}", response);

        return RsData.success(response);
    }

    // accessToken Valified Check
    @PostMapping("/validate")
    public ResponseEntity<RsData<Boolean>> validateToken(@Valid @RequestBody TokenValidateRequest request) {
        boolean isValid = JwtUtil.validateToken(request.accessToken());
        return RsData.success(isValid);
    }

}
