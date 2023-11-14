package com.tuananhdo.security.oauth;

import com.tuananhdo.entity.User;
import com.tuananhdo.service.UserAuthenticationService;
import com.tuananhdo.utils.AuthenticationType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UserAuthenticationService userAuthenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2BlogUser oAuth2User = (OAuth2BlogUser) authentication.getPrincipal();
        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String clientName = oAuth2User.getClientName();
        LOGGER.info("OAuth2LoginSuccessHandler: " + name + " " + email);
        LOGGER.info("clientName: " + clientName);
        AuthenticationType authenticationType = getAuthenticationType(clientName);
        User user = userAuthenticationService.findByEmail(email);
        if (user == null) {
            userAuthenticationService.createUserOAuthLogin(name, email, authenticationType);
        } else {
            userAuthenticationService.updateAuthentication(user, getAuthenticationType(clientName));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        return AuthenticationType.valueOf(clientName.toUpperCase());
    }
}
