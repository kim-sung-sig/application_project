package com.example.userservice.application.auth.response;

public record JwtTokenResponse(
    String accessToken,
    String refreshToken
) {}
