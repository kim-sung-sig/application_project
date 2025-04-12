package com.example.userservice.api.auth.response;

public record JwtTokenResponse(
    String accessToken,
    String refreshToken
) {}
