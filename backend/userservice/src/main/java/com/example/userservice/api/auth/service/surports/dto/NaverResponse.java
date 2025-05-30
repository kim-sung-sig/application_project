package com.example.userservice.api.auth.service.surports.dto;

import java.util.Map;

import com.example.userservice.api.auth.service.surports.OAuth2Response;

import lombok.ToString;

@ToString
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    @SuppressWarnings("unchecked")
    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.getOrDefault("id", "").toString();
    }

    @Override
    public String getEmail() {
        return attribute.getOrDefault("email", "").toString();
    }

    @Override
    public String getName() {
        return attribute.getOrDefault("name", "").toString();
    }

    @Override
    public String getNickName() {
        return attribute.getOrDefault("nickname", "").toString();
    }
}
