package com.example.userservice.common.config.securiry.oauth2;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.example.userservice.common.config.securiry.oauth2.dto.GoogleResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.NaverResponse;
import com.example.userservice.common.config.securiry.oauth2.dto.OAuth2Response;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oauth2Response = switch(registrationId){
            case "naver" -> new NaverResponse(oauth2User.getAttributes());
            case "google" -> new GoogleResponse(oauth2User.getAttributes());
            default -> null;
        };

        if(oauth2Response == null){
            throw new OAuth2AuthenticationException("지원하지 않는 플랫폼입니다.");
        }

        User user = saveOrUpdateAndGetUser(oauth2Response);

        return oauth2User;
    }

    private User saveOrUpdateAndGetUser(OAuth2Response oauth2Response){

        String username = oauth2Response.getProvider()+"_"+oauth2Response.getProviderId();

        Optional<User> userOptional = userRepository.findByUsername(username);

        User user;
        if (userOptional.isPresent()){
            user = userOptional.get();
            user.changeNickName(oauth2Response.getName());
            user.changeEmail(oauth2Response.getEmail());
        }
        else {
            user = User.builder()
                    .username(username)
                    .password(null)
                    .nickName(username)
                    .email(oauth2Response.getEmail())
                    .role(UserRole.ROLE_USER)
                    .build();
        }

        return userRepository.save(user);
    }
}
