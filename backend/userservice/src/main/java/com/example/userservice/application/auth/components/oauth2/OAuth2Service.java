package com.example.userservice.application.auth.components.oauth2;

import com.example.userservice.api.auth.request.OAuthRequest;
import com.example.userservice.application.auth.components.oauth2.dto.OAuth2Response;

public interface OAuth2Service {

    OAuth2Response getUserInfo(OAuthRequest oauthRequest);

    String getAccessToken(OAuthRequest oauthRequest);

    OAuth2Response getUserInfo(String accessToken);

}
