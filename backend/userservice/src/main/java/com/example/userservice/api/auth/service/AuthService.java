package com.example.userservice.api.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.userservice.api.auth.repository.RefreshTokenRepository;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.user.domain.entity.User.UserStatus;
import com.example.userservice.api.user.domain.model.UserForSecurity;
import com.example.userservice.api.user.domain.repository.user.UserRepository;
import com.example.userservice.common.constants.ConstantsUtil;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.common.util.JwtUtil;
import com.example.userservice.common.util.PasswordUtil;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    // repository
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /** 토큰 발급 (username, password)
     * @param loginRequest
     * @return
     */
    @Transactional
    public JwtTokenResponse createTokenByUsernameAndPassword(String inputUsername, String inputPassword) {
        Optional<UserForSecurity> userOp = userRepository.findByUsernameForSecurity(inputUsername);

        // username에 일치하는 사용자가 존재하는지 확인
        if (!userOp.isPresent()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserForSecurity user = userOp.get();
        // 사용자가 잠긴 사용자 인지 확인
        if (Objects.equal(user.status(), UserStatus.LOCKED)) {
            throw new LockedException("User account is locked");
        }

        // 사용자의 비밀번호 일치여부 확인
        if (!PasswordUtil.matches(inputPassword, user.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return createToken(user);
    }

    /** 토큰 발급 (refreshToken)
     * @param refreshToken
     * @return
     */
    @Transactional
    public JwtTokenResponse createTokenByRefreshToken(String refreshToken) {
        // 1. 토큰이 비어있으면 400 에러
        if (CommonUtil.isEmpty(refreshToken)) {
            log.debug("[TOKEN ERROR] Refresh token is missing");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is missing");
        }

        // 2. 토큰 검증 실패 시 401 에러
        if (!JwtUtil.validateToken(refreshToken)) {
            log.debug("[TOKEN ERROR] Refresh token({}) is unvalid or expired", refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        // 3. 토큰 저장소 조회 (없으면 401 에러)
        UUID userId = refreshTokenRepository.getUserIdFromRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.debug("[TOKEN ERROR] Refresh token({}) not found in repository", refreshToken);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
                });

        // 4. 유저 조회 (없으면 401 에러)
        UserForSecurity user = userRepository.findByUserIdForSecurity(userId)
                .orElseThrow(() -> {
                    log.debug("[USER ERROR] User not found for refreshToken: {}, userId: {}", refreshToken, userId);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });

        // 5. 계정이 잠겼다면
        if (Objects.equal(user.status(), UserStatus.LOCKED)) {
            log.warn("[SECURITY WARNING] Locked user attempted to refresh token. userId: {}, refreshToken: {}", userId, refreshToken);
            throw new LockedException("User account is locked");
        }

        // 6. 새로운 토큰 발급 (성공 로그)
        log.info("[TOKEN SUCCESS] Refresh token({}) validated. Issuing new token for userId: {}", refreshToken, userId);

        var response = createToken(user);
        log.info("[TOKEN SUCCESS] New token issued. userId: {}, accessToken: {}, refreshToken: {}", userId, response.accessToken(), response.refreshToken());
        return response;
    }

    private JwtTokenResponse createToken(UserForSecurity user) {

        // 기존 리프래쉬 토큰이 있다면 삭제
        refreshTokenRepository.deleteRefreshToken(user.id());

        // 새로운 토큰 발급
        String accessToken = JwtUtil.generateToken(user, ConstantsUtil.ACCESS_TOKEN_TTL);
        String refreshToken = JwtUtil.generateRefreshToken(ConstantsUtil.REFRESH_TOKEN_TTL);
        refreshTokenRepository.saveRefreshToken(refreshToken, user.id()); // 저장소에 토큰저장

        // 토큰 반환
        return new JwtTokenResponse(accessToken, refreshToken);
    }

}
