package com.tuananhdo.security;

import com.tuananhdo.entity.User;
import com.tuananhdo.service.UserService;
import com.tuananhdo.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLoginFailureHandler.class);
    public static final int MAX_FAILED_ATTEMPTS = 20;

    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        User user = userService.getByEmail(email);
        if (Objects.nonNull(user)) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < MAX_FAILED_ATTEMPTS) {
                    userService.increaseFailedAttempt(user);
                } else {
                    userService.lock(user);
                    request.getSession().setAttribute("message", "You account has been locked due to" +
                            StringUtil.toLowerCase(String.valueOf(MAX_FAILED_ATTEMPTS)) + " failed attempts, Please try again later");
                }
            } else if (!user.isAccountNonLocked() && Objects.isNull(user.getVerificationCode())) {
                userService.unlock(user);
            }
            LOGGER.error("User failed to login: " + email);
        } else {
            LOGGER.error("Email not found: " + email);
        }
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
