package com.example.userservice.api.auth.exception;

public class CustomAuthException extends RuntimeException{

    private final AuthErrorCode code;

    public CustomAuthException(AuthErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public AuthErrorCode getCode() {
        return code;
    }

    public enum AuthErrorCode {
        BAD_CREDENTIALS,
        ACCOUNT_LOCKED,
        REFRESH_TOKEN_EXPIRED,
        USER_NOT_FOUND,
        OAUTH2_AUTH_FAILED
    }

}
