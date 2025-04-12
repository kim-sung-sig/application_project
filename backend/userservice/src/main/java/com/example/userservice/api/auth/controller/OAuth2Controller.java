package com.example.userservice.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.OAuth2Service;
import com.example.userservice.common.response.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @PostMapping("/login")
    public ResponseEntity<RsData<JwtTokenResponse>> oauthLogin(@Valid @RequestBody OAuthRequest oauthRequest) {
        log.debug("oauth login request : {}", oauthRequest);

        JwtTokenResponse response = oAuth2Service.createTokenByOAuth(oauthRequest);
        log.debug("oauth login response : {}", response);

        return RsData.success(response);
    }

}
