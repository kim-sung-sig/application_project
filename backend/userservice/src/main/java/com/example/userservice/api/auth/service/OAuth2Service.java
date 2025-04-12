package com.example.userservice.api.auth.service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.userservice.api.auth.repository.RefreshTokenRepository;
import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.surports.OAuth2Response;
import com.example.userservice.api.auth.service.surports.OAuth2ServiceInterface;
import com.example.userservice.api.user.components.NickNameTagGenerator;
import com.example.userservice.common.constants.ConstantsUtil;
import com.example.userservice.common.util.CommonUtil;
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
    private final Map<String, OAuth2ServiceInterface> oAuth2Services;

    @Transactional
    public JwtTokenResponse createTokenByOAuth(OAuthRequest oauthRequest) {
        log.info("createTokenByOAuth({}) 호출", oauthRequest);

        OAuth2Response oauth2Response = getUserInfo(oauthRequest);

        if (CommonUtil.isEmpty(oauth2Response))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");

        log.debug("oauth response : {}", oauth2Response);

        // 사용자 정보 저장
        UserForSecurity user = saveOrUpdateUserAndGet(oauth2Response);

        // 토큰 발급
        return createToken(user);
    }

    private OAuth2Response getUserInfo(OAuthRequest oauthRequest) {
        String provider = oauthRequest.provider().toLowerCase();

        return Optional.ofNullable(oAuth2Services.get(provider))
                .map(service -> service.getUserInfo(oauthRequest))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인입니다."));
    }

    private UserForSecurity saveOrUpdateUserAndGet(OAuth2Response oauth2Response) {
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
            String uniqueNickName = nickNameTagGenerator.generateTag(oauth2Response.getNickName());
            user.changeNickName(uniqueNickName);
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
