package com.example.userservice.api.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.userservice.api.auth.repository.RefreshTokenRepository;
import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.surports.OAuth2Response;
import com.example.userservice.api.auth.service.surports.SocialOAuth2Service;
import com.example.userservice.api.user.components.NickNameTagGenerator;
import com.example.userservice.common.constants.ConstantsUtil;
import com.example.userservice.common.util.JwtUtil;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.model.UserForSecurity;
import com.example.userservice.domain.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    // repository
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // util
    private final NickNameTagGenerator nickNameTagGenerator;

    // oauth2
    private final Map<String, SocialOAuth2Service> socialServices;

    @Transactional
    public JwtTokenResponse createTokenByOAuth(@NonNull OAuthRequest oauthRequest) {
        log.info("createTokenByOAuth({}) 호출", oauthRequest);

        OAuth2Response oauth2Response = getUserInfo(oauthRequest);
        log.debug("oauth response : {}", oauth2Response);

        // 사용자 정보 저장
        UserForSecurity user = saveOrUpdateUserAndGet(oauth2Response);

        // 토큰 발급
        return createToken(user);
    }

    private OAuth2Response getUserInfo(OAuthRequest oauthRequest) {
        String provider = oauthRequest.provider().toLowerCase();

        return Optional.ofNullable(socialServices.get(provider))
                .map(service -> service.getUserInfo(oauthRequest))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인입니다."));
    }

    private UserForSecurity saveOrUpdateUserAndGet(@NonNull OAuth2Response oauth2Response) {
        User user = findOrCreateUser(oauth2Response);
        User saved = userRepository.save(user);

        return UserForSecurity.of(saved);
    }

    private User findOrCreateUser(OAuth2Response oauth2Response) {
        String username = oauth2Response.getProvider() + "_" + oauth2Response.getProviderId();

        return userRepository.findByUsername(username)
                .map(existingUser -> updateUserIfNeeded(existingUser, oauth2Response))
                .orElseGet(() -> createNewUser(oauth2Response, username));
    }

    private User updateUserIfNeeded(User user, OAuth2Response oauth2Response) {
        if (!Objects.equals(user.getNickName(), oauth2Response.getNickName())) {
            String nickName = oauth2Response.getNickName();
            String uniqueNickNameTag = nickNameTagGenerator.generateTag(nickName);
            user.changeNickName(nickName, uniqueNickNameTag);
        }
        user.changeEmail(oauth2Response.getEmail());
        return user;
    }

    private User createNewUser(OAuth2Response oauth2Response, String username) {
        String uniqueNickName = nickNameTagGenerator.generateTag(oauth2Response.getNickName());

        return User.builder()
                .username(username)
                .password(null)
                .name(oauth2Response.getName())
                .nickName(uniqueNickName)
                .email(oauth2Response.getEmail())
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ENABLED)
                .build();
    }

    private JwtTokenResponse createToken(UserForSecurity user) {
        AtomicReference<String> refreshToken = new AtomicReference<>();

        refreshTokenRepository.getRefreshTokenByUserId(user.id())
                .ifPresentOrElse(existedToken -> {
                    Date expiration = JwtUtil.getExpiration(existedToken);

                    // 만료일이 1달미만으로 남은 경우 재발급
                    if (isExpiringWithinOneMonth(expiration)) {
                        String newToken = JwtUtil.generateRefreshToken(ConstantsUtil.REFRESH_TOKEN_TTL);
                        refreshTokenRepository.deleteRefreshToken(user.id());
                        refreshTokenRepository.saveRefreshToken(newToken, user.id());
                        refreshToken.set(JwtUtil.generateRefreshToken(ConstantsUtil.REFRESH_TOKEN_TTL));
                    }
                    else refreshToken.set(existedToken);
                }, () -> {
                    String newToken = JwtUtil.generateRefreshToken(ConstantsUtil.REFRESH_TOKEN_TTL);
                    refreshTokenRepository.saveRefreshToken(newToken, user.id());
                    refreshToken.set(newToken);
                });;

        // 새로운 토큰 발급
        String accessToken = JwtUtil.generateToken(user, ConstantsUtil.ACCESS_TOKEN_TTL);

        // 토큰 반환
        return new JwtTokenResponse(accessToken, refreshToken.get());
    }

    // 유틸 메서드: 1달 이내인지 확인
    private boolean isExpiringWithinOneMonth(Date expiration) {
        LocalDateTime expDateTime = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime oneMonthLater = LocalDateTime.now().plusMonths(1);
        return expDateTime.isBefore(oneMonthLater);
    }

}
