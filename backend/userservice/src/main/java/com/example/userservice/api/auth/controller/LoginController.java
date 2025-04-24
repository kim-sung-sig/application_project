package com.example.userservice.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.request.TokenRefreshRequest;
import com.example.userservice.api.auth.request.TokenValidateRequest;
import com.example.userservice.api.auth.request.UserLoginRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.AuthService;
import com.example.userservice.api.auth.service.OAuth2Service;
import com.example.userservice.common.config.securiry.model.SecurityUser;
import com.example.userservice.common.response.ApiResponse;
import com.example.userservice.common.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;
    private final OAuth2Service oAuth2Service;

    // 토큰 발급 with (username, password)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse< JwtTokenResponse >> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        log.debug("login request : {}", loginRequest);

        JwtTokenResponse token = authService.createTokenByUsernameAndPassword(loginRequest.username(), loginRequest.password());
        log.debug("login response : {}", token);

        return ApiResponse.success(token);
    }

    // 토큰 발급 with (소셜로그인)
    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> oauthLogin(@Valid @RequestBody OAuthRequest oauthRequest) {
        log.debug("oauth login request : {}", oauthRequest);

        JwtTokenResponse response = oAuth2Service.createTokenByOAuth(oauthRequest);
        log.debug("oauth login response : {}", response);

        return ApiResponse.success(response);
    }

    // 토큰 발급 with (refresh token)
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse< JwtTokenResponse >> refreshToken(@Valid @RequestBody TokenRefreshRequest refreshToken) {
        log.debug("refresh request : {}", refreshToken);

        JwtTokenResponse response = authService.createTokenByRefreshToken(refreshToken.refreshToken());
        log.debug("refresh response : {}", response);

        return ApiResponse.success(response);
    }

    // accessToken Valified Check
    @PostMapping("/token/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@Valid @RequestBody TokenValidateRequest request) {
        boolean isValid = JwtUtil.validateToken(request.accessToken());
        return ApiResponse.success(isValid);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal SecurityUser userDetails) {
        log.debug("logout user : {}", userDetails);

        authService.logout(userDetails.id());
        return ApiResponse.success();
    }
}
