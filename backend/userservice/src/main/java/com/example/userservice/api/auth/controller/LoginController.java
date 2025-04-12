package com.example.userservice.api.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.userservice.api.auth.request.UserLoginRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.AuthService;
import com.example.userservice.common.constants.ConstantsUtil;
import com.example.userservice.common.response.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestControllerAdvice(basePackageClasses = LoginController.class)
public class LoginController {

    private final AuthService authService;

    // 토큰 발급 with (username, password)
    @PostMapping("/login")
    public ResponseEntity<RsData< JwtTokenResponse >> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        log.debug("login request : {}", loginRequest);

        JwtTokenResponse token = authService.createTokenByUsernameAndPassword(loginRequest);
        log.debug("login response : {}", token);

        return RsData.success(token);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(LockedException ex) {
        Map<String, Object> errorResponse = Map.of(
            ConstantsUtil.RETURN_MESSAGE, "사용자의 계정이 잠겼습니다.",
            "errorCode", "ACCOUNT_LOCKED"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> errorResponse = Map.of(
            ConstantsUtil.RETURN_MESSAGE, "아이디 또는 비밀번호가 일치하지 않습니다."
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

}
