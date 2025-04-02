package com.example.userservice.api.auth.request;

public record OAuthRequest(
    String provider,
    String accessToken
) {}
