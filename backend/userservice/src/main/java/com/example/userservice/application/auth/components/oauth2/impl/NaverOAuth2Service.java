package com.example.userservice.application.auth.components.oauth2.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.application.auth.components.oauth2.OAuth2Service;
import com.example.userservice.application.auth.components.oauth2.dto.NaverResponse;
import com.example.userservice.application.auth.components.oauth2.dto.OAuth2Response;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOAuth2Service implements OAuth2Service {

    private final RestClient restClient = RestClient.create();

    @Value("${oauth.naver.client-id}")
    private String naverClientId;

    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;

    @PostConstruct
    public void init() {
        log.debug("naverClientId: " + naverClientId);
        log.debug("naverClientSecret: " + naverClientSecret);
    }

    @Override
    public OAuth2Response getUserInfo(OAuthRequest oauthRequest) {
        String accessToken = this.getAccessToken(oauthRequest);
        return this.getUserInfo(accessToken);
    }

    @Override
    public String getAccessToken(OAuthRequest oauthRequest) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", oauthRequest.code());
        params.add("state", oauthRequest.state());

        ResponseEntity<Map<String, Object>> response = restClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();

            if (body == null) throw new RuntimeException("Failed to get access token");

            String accessToken = (String) body.get("access_token");
            return accessToken;
        }
        else if (response.getStatusCode().is4xxClientError()) throw new RuntimeException("Failed to get access token");
        else throw new RuntimeException("Failed to get access token");
    }

    @Override
    public OAuth2Response getUserInfo(String accessToken) {
        ResponseEntity<Map<String, Object>> response = restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();

            if (body == null) throw new RuntimeException("Failed to get user info");

            return new NaverResponse(body);
        }
        else if (response.getStatusCode().is4xxClientError()) throw new RuntimeException("Failed to get user info");
        else throw new RuntimeException("Failed to get user info");
    }

}
