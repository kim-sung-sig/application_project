package com.example.userservice.common.config.securiry.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.api.user.domain.entity.User;
import com.example.userservice.api.user.domain.exception.UserNotFoundException;
import com.example.userservice.api.user.domain.model.UserForSecurity;
import com.example.userservice.api.user.domain.repository.user.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry = new SessionRegistryImpl();

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        /*
        log.info("로그인 성공" + authentication.getPrincipal());

        // 기존 세션 강제 종료
        sessionRegistry.getAllSessions(authentication.getPrincipal(), false)
                .forEach(SessionInformation::expireNow);;

        CustomUserDetails userDatails = (CustomUserDetails) authentication.getPrincipal();
        UserForSecurity userForSecurity = userDatails.getUser();

        processUserLoggedIn(userForSecurity);

        request.getSession().setAttribute("user", userForSecurity);
		request.getSession().setMaxInactiveInterval(60 * 30);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        // JSON 응답 데이터 생성
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("redirectUrl", getReturnUrl(request, response));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(data);

        // 응답을 작성
        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
            writer.flush();
        }
         */
    }

    private String getReturnUrl(HttpServletRequest request, HttpServletResponse response) {
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String sessionRedirectUrl = (String) request.getSession().getAttribute("redirectUrl");

        String redirectUrl = (sessionRedirectUrl != null) ? sessionRedirectUrl :
                            (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";
        log.debug("Redirecting to URL: {}", redirectUrl); // 디버깅용 로그 추가
        return redirectUrl;
    }

    private void processUserLoggedIn(UserForSecurity userForSecurity) {
        User loggedInUser = userRepository.findByUsername(userForSecurity.username())
                .orElseThrow(() -> new UserNotFoundException());

        loggedInUser.loginSuccess();
        // userRepository.save(loggedInUser);
    }
}
