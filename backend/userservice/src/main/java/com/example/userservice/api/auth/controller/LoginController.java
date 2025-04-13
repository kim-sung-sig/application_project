package com.example.userservice.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.request.UserLoginRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.AuthService;
import com.example.userservice.api.auth.service.OAuth2Service;
import com.example.userservice.common.response.RsData;

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
    public ResponseEntity<RsData< JwtTokenResponse >> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        log.debug("login request : {}", loginRequest);

        JwtTokenResponse token = authService.createTokenByUsernameAndPassword(loginRequest.username(), loginRequest.password());
        log.debug("login response : {}", token);

        return RsData.success(token);
    }

    // 토큰 발급 with (소셜로그인)
    @PostMapping("/oauth/login")
    public ResponseEntity<RsData<JwtTokenResponse>> oauthLogin(@Valid @RequestBody OAuthRequest oauthRequest) {
        log.debug("oauth login request : {}", oauthRequest);

        JwtTokenResponse response = oAuth2Service.createTokenByOAuth(oauthRequest);
        log.debug("oauth login response : {}", response);

        return RsData.success(response);
    }

}
