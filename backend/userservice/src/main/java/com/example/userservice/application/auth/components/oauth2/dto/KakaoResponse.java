package com.example.userservice.application.auth.components.oauth2.dto;

import java.util.Map;

import lombok.ToString;

@ToString
public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        throw new UnsupportedOperationException("Unimplemented method 'getProviderId'");
    }

    @Override
    public String getEmail() {
        throw new UnsupportedOperationException("Unimplemented method 'getEmail'");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public String getNickName() {
        throw new UnsupportedOperationException("Unimplemented method 'getNickName'");
    }
    
    
}
