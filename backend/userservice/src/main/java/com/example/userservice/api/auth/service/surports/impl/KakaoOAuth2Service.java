package com.example.userservice.api.auth.service.surports.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.service.surports.OAuth2Response;
import com.example.userservice.api.auth.service.surports.OAuth2ServiceInterface;
import com.example.userservice.api.auth.service.surports.dto.KakaoResponse;
import com.example.userservice.common.util.JwtUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("kakao")
public class KakaoOAuth2Service implements OAuth2ServiceInterface {

    private final RestClient restClient = RestClient.create();

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @PostConstruct
    public void init() {
        log.debug("kakaoClientId: " + kakaoClientId);
        log.debug("kakaoClientSecret: " + kakaoClientSecret);
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
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("code", oauthRequest.code());
        params.add("state", oauthRequest.state());

        ResponseEntity<Map<String, Object>> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8).toString())
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
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.BEARER_PREFIX + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8).toString())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();

            if (body == null) throw new RuntimeException("Failed to get user info");

            return new KakaoResponse(body);
        }
        else if (response.getStatusCode().is4xxClientError()) throw new RuntimeException("Failed to get user info");
        else throw new RuntimeException("Failed to get user info");
    }

}
