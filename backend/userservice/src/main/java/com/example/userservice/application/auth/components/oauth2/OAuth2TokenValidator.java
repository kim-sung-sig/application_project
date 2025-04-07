package com.example.userservice.application.auth.components.oauth2;

import org.springframework.stereotype.Component;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.application.auth.components.oauth2.dto.OAuth2Response;
import com.example.userservice.application.auth.components.oauth2.impl.KakaoOAuth2Service;
import com.example.userservice.application.auth.components.oauth2.impl.NaverOAuth2Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2TokenValidator {

    private final NaverOAuth2Service naverOAuth2Service;
    private final KakaoOAuth2Service kakaoOAuth2Service;

    public OAuth2Response getUserInfo(OAuthRequest oauthRequest) {
        return switch (oauthRequest.provider().toLowerCase()) {
            case "naver" -> naverOAuth2Service.getUserInfo(oauthRequest);
            case "kakao" -> kakaoOAuth2Service.getUserInfo(oauthRequest);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        };
    }

}
