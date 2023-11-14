package com.tuananhdo.security;

import com.tuananhdo.entity.User;
import com.tuananhdo.service.UserAuthenticationService;
import com.tuananhdo.utils.AuthenticationType;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserAuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomUserDetails currentUser = Objects.requireNonNull(SecurityUtils.getCurrentUser());
        User user = currentUser.getUser();
        authenticationService.updateAuthentication(user, AuthenticationType.DATABASE);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
