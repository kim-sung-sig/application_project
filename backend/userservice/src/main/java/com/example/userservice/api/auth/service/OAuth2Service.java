package com.example.userservice.api.auth.service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.userservice.api.auth.components.JwtTokenComponent;
import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.api.auth.response.JwtTokenResponse;
import com.example.userservice.api.auth.service.surports.OAuth2Response;
import com.example.userservice.api.auth.service.surports.SocialOAuth2Service;
import com.example.userservice.api.user.components.NickNameTagGenerator;
import com.example.userservice.common.config.securiry.dto.SecurityUser;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
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

    private final Map<String, SocialOAuth2Service> socialServices;  // oauth2 component
    private final NickNameTagGenerator nickNameTagGenerator;        // nickName component
    private final JwtTokenComponent jwtTokenComponent;              // jwt component

    @Transactional
    public JwtTokenResponse createTokenByOAuth(@NonNull OAuthRequest oauthRequest) {
        log.info("createTokenByOAuth({}) 호출", oauthRequest);

        // 소셜 로그인 사용자 정보 추출
        OAuth2Response oauth2Response = getUserInfo(oauthRequest);
        log.debug("oauth response : {}", oauth2Response);

        // 사용자 정보 저장 혹은 업데이트
        User socialUser = saveOrUpdateUserAndGet(oauth2Response);

        // 사용자 정보기반 인증 객체 생성
        SecurityUser securityUser = SecurityUser.of(socialUser);

        // 토큰 발급
        JwtTokenResponse token = jwtTokenComponent.createToken(securityUser);
        log.info("[TOKEN SUCCESS] New token issued. userId: {}, accessToken: {}, refreshToken: {}", securityUser.id(), token.accessToken(), token.refreshToken());
        return token;
    }

    private OAuth2Response getUserInfo(OAuthRequest oauthRequest) {
        String provider = oauthRequest.provider().toLowerCase();

        return Optional.ofNullable(socialServices.get(provider))
                .map(service -> service.getUserInfo(oauthRequest))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인입니다."));
    }

    private User saveOrUpdateUserAndGet(@NonNull OAuth2Response oauth2Response) {
        User user = findOrCreateUser(oauth2Response);
        User saved = userRepository.save(user);

        return saved;
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

}
