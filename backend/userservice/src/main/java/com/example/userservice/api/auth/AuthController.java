package com.example.userservice.api.auth;

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

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.request.TokenRefresh;
import com.example.userservice.api.auth.request.UserLoginRequest;
import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.application.auth.AuthService;
import com.example.userservice.application.auth.response.JwtTokenResponse;
import com.example.userservice.common.constants.ConstantsUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthController {

    private final AuthService authService;

    // 토큰 발급 with (username, password)
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody UserLoginRequest loginRequest) {
        log.debug("login request : {}", loginRequest);
        JwtTokenResponse response = authService.createTokenByUsernameAndPassword(loginRequest);
        log.debug("login response : {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<JwtTokenResponse> oauthLogin(@RequestBody OAuthRequest oauthRequest) {
        log.debug("oauth login request : {}", oauthRequest);
        JwtTokenResponse response = authService.createTokenByOAuth(oauthRequest);
        log.debug("oauth login response : {}", response);
        return ResponseEntity.ok(response);
    }

    // 토큰 발급 with (refresh token)
    @PostMapping("/token/refresh")
    public ResponseEntity<JwtTokenResponse> refreshToken(@RequestBody TokenRefresh refreshToken) {
        JwtTokenResponse response = authService.createTokenByRefreshToken(refreshToken.refreshToken());
        return ResponseEntity.ok(response);
    }

    // 회원 가입
    @PostMapping("/signup")
    public void signUp(CreateUserCommand request) {
        log.info("회원 가입 요청");
    }


    // 비밀 번호 재설정 요청
    @PostMapping("/password-reset-request")
    public void passwordResetRequest() {
        log.info("비밀번호 재설정 요청");
    }

    // 비밀 번호 재설정
    @PostMapping("/password-reset")
    public void passwordReset() {
        log.info("비밀번호 재설정");
    }

    // 회원 탈퇴
    @PostMapping("/withdraw")
    public void withdraw() {
        log.info("회원 탈퇴 요청");
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
