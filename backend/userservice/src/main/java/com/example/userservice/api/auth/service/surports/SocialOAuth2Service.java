package com.example.userservice.api.auth.service.surports;

import com.example.userservice.api.auth.request.OAuthRequest;

public interface SocialOAuth2Service {

    OAuth2Response getUserInfo(OAuthRequest oauthRequest);

    String getAccessToken(OAuthRequest oauthRequest);

    OAuth2Response getUserInfo(String accessToken);

}
