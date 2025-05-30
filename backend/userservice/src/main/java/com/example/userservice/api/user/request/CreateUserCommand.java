package com.example.userservice.api.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateUserCommand(

    @NotEmpty
    @Size(min = 8, max = 20)
    String username,

    @NotEmpty
    @Size(min = 8, max = 20)
    String password,

    @NotEmpty
    @Size(min = 2, max = 10)
    String name,

    @NotEmpty
    @Size(min = 2, max = 16)
    String nickName,

    @Email
    @NotEmpty
    String email

) {
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NICKNAME = "nickName";
    public static final String FIELD_EMAIL = "email";
}
