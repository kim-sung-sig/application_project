package com.example.userservice.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
