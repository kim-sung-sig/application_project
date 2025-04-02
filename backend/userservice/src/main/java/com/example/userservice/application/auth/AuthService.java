package com.example.userservice.application.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.request.UserLoginRequest;
import com.example.userservice.application.auth.components.OAuth2TokenValidator;
import com.example.userservice.application.auth.components.RefreshTokenRepository;
import com.example.userservice.application.auth.response.JwtTokenResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.OAuth2Response;
import com.example.userservice.common.constants.ConstantsUtil;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.common.util.JwtUtil;
import com.example.userservice.domain.entity.NickNameHistory;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.model.UserForSecurity;
import com.example.userservice.domain.repository.history.NickNameHistoryRepository;
import com.example.userservice.domain.repository.user.UserRepository;
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
    private final NickNameHistoryRepository nickNameHistoryRepository;
    // private final PasswordHistoryRepository passwordHistoryRepository;

    // component
    private final OAuth2TokenValidator oauth2TokenValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * 토큰 발급 (username, password)
     * 
     * @param loginRequest
     * @return
     */
    @Transactional
    public JwtTokenResponse createTokenByUsernameAndPassword(UserLoginRequest loginRequest) {
        Optional<UserForSecurity> userOp = userRepository.findByUsernameForSecurity(loginRequest.username());

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
        if (!passwordEncoder.matches(loginRequest.password(), user.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return createToken(user);
    }

    /**
     * 소셜 로그인 토큰 발급 (provider, accessToken)
     * 
     * @param oauthRequest
     * @return
     */
    @Transactional
    public JwtTokenResponse createTokenByOAuth(OAuthRequest oauthRequest) {
        if (CommonUtil.isEmpty(oauthRequest))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");

        log.info("createTokenByOAuth({}) 호출", oauthRequest);
        String provider = oauthRequest.provider();
        String accessToken = oauthRequest.accessToken();

        OAuth2Response oauth2Response = oauth2TokenValidator.getUserInfo(provider, accessToken);
        if (CommonUtil.isEmpty(oauth2Response))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        log.debug("oauth response : {}", oauth2Response);

        // 사용자 정보 저장
        UserForSecurity user = saveOrUpdateUserAndGet(oauth2Response);

        // 토큰 발급
        return createToken(user);
    }

    /**
     * 토큰 발급 (refreshToken)
     * 
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

    private UserForSecurity saveOrUpdateUserAndGet(OAuth2Response oauth2Response) {

        String username = oauth2Response.getProvider() + "_" + oauth2Response.getProviderId();
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.changeNickName(oauth2Response.getName());
            user.changeEmail(oauth2Response.getEmail());

        } else {
            String nickName = oauth2Response.getName();
            NickNameHistory nickNameHistory = nickNameHistoryRepository.findByNickNameForUpdate(nickName)
                    .orElseGet(() -> {
                        NickNameHistory newHistory = new NickNameHistory(nickName, 0L);
                        return nickNameHistoryRepository.save(newHistory);
                    });

            long newSeq = nickNameHistory.incrementSeqAndGet(); // 시퀀스 증가
            nickNameHistoryRepository.save(nickNameHistory);

            user = User.builder()
                    .username(username)
                    .password(null)
                    .nickName(username + newSeq)
                    .email(oauth2Response.getEmail())
                    .role(UserRole.ROLE_USER)
                    .build();
        }

        User saveUser = userRepository.save(user);

        return userRepository.findByUserIdForSecurity(saveUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found"));
    }

    // 아이디 찾기
    @Transactional(readOnly = true)
    public String findUsername(String email) {
        return null;
    }

    /**
     * 임시 비밀번호 발급하기
     * 
     * @param token
     * @param inputPassword
     */
    @Transactional
    public void issueTemporaryPassword(String username, String email) {
        // 1. 이메일로 사용자 찾기
        // 2. 임시 비밀번호 생성
        // 3. 임시 비밀번호 암호화
        // 4. 임시 비밀번호 저장
        // 5. 이메일로 임시 비밀번호 전송
    }

    // 임시 비밀번호 확인하기
    @Transactional
    public boolean verifyTemporaryPassword(String token) {
        return false;
    }

    // 임시 비밀번호 확인후 비밀번호 변경하기
    @Transactional
    public void changePasswordAfterVerification(String token, String newPassword) {

    }

}
