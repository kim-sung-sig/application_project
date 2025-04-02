package com.example.userservice.application.auth.components;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.userservice.application.auth.exception.OAuth2AuthenticationException;
import com.example.userservice.common.config.securiry.oauth2.dto.GoogleResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.NaverResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.OAuth2Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2TokenValidator {

    private final RestTemplate restTemplate;

    public OAuth2Response getUserInfo(String provider, String accessToken) {
        return switch (provider.toLowerCase()) {
            case "google" -> validateGoogleToken(accessToken);
            case "naver" -> validateNaverToken(accessToken);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        };
    }

    private OAuth2Response validateGoogleToken(String accessToken) {
        final String provider = "google";
        final String url = "https://www.googleapis.com/oauth2/v3/userinfo";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );

            return new GoogleResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("{} OAuth2 인증 실패 - 클라이언트 오류: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("유효하지 않은 액세스 토큰입니다.", e);
        } catch (HttpServerErrorException e) {
            log.error("{} OAuth2 서버 오류 발생: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("Google 서버에서 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("{} OAuth2 요청 중 알 수 없는 오류 발생: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("Google 인증 과정에서 오류가 발생했습니다.", e);
        }
    }

    private OAuth2Response validateNaverToken(String accessToken) {
        final String provider = "naver";
        final String url = "https://openapi.naver.com/v1/nid/me";

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );

            return new NaverResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("{} OAuth2 인증 실패 - 클라이언트 오류: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("유효하지 않은 액세스 토큰입니다.", e);
        } catch (HttpServerErrorException e) {
            log.error("{} OAuth2 서버 오류 발생: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("Naver 서버에서 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("{} OAuth2 요청 중 알 수 없는 오류 발생: {}", provider, e.getMessage(), e);
            throw new OAuth2AuthenticationException("Naver 인증 과정에서 오류가 발생했습니다.", e);
        }

    }

}
