package com.example.userservice.application.auth.components;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.userservice.common.config.securiry.oauth2.dto.NaverResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.OAuth2Response;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverOAuthService {

    private final RestClient restClient;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public OAuth2Response getUserInfo(String accessToken) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", accessToken);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<Map<String, Object>> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

        return new NaverResponse(response.getBody());
    }
}
