package com.example.userservice.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthRequest(
    @NotBlank
    String provider,

    @NotBlank
    String code,

    @NotBlank
    String state        // 네이버는 필수, 카카오는 선택

) {}
