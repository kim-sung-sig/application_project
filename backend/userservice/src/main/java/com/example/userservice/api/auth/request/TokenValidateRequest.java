package com.example.userservice.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record TokenValidateRequest(
    @NotBlank String accessToken
) {}
