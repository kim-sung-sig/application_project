package com.example.userservice.api.auth.service.surports.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.userservice.api.auth.exception.CustomAuthException;
import com.example.userservice.api.auth.exception.CustomAuthException.AuthErrorCode;
import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.service.surports.OAuth2Response;
import com.example.userservice.api.auth.service.surports.SocialOAuth2Service;
import com.example.userservice.api.auth.service.surports.dto.NaverResponse;
import com.example.userservice.common.exception.TemporaryException;
import com.example.userservice.common.util.JwtUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("naver")
@RequiredArgsConstructor
public class NaverOAuth2Service implements SocialOAuth2Service {

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
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("4xx error during(get access token) request to Naver. Request: {}, Response status: {}, Response: {}", req, res.getStatusCode(), res);
                    throw new CustomAuthException(AuthErrorCode.OAUTH2_AUTH_FAILED, "Failed to get access token");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("5xx error during(get access token) request to Naver. Request: {}, Response status: {}, Response: {}", req, res.getStatusCode(), res);
                    throw new TemporaryException(5);
                })
                .toEntity(new ParameterizedTypeReference<>() {});

        Map<String, Object> body = response.getBody();

        if (body == null) throw new CustomAuthException(AuthErrorCode.OAUTH2_AUTH_FAILED, "Failed to get access token");

        String accessToken = (String) body.get("access_token");
        return accessToken;
    }

    @Override
    public OAuth2Response getUserInfo(String accessToken) {
        ResponseEntity<Map<String, Object>> response = restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.BEARER_PREFIX + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("4xx error during(get userInfo) request to Naver. Request: {}, Response status: {}, Response: {}", req, res.getStatusCode(), res);
                    throw new CustomAuthException(AuthErrorCode.OAUTH2_AUTH_FAILED, "Failed to get userInfo");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("5xx error during(get userInfo) request to Naver. Request: {}, Response status: {}, Response: {}", req, res.getStatusCode(), res);
                    throw new TemporaryException(5);
                })
                .toEntity(new ParameterizedTypeReference<>() {});

        Map<String, Object> body = response.getBody();

        if (body == null) throw new CustomAuthException(AuthErrorCode.OAUTH2_AUTH_FAILED, "Failed to get user info");

        return new NaverResponse(body);
    }

}
