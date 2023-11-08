package com.tuananhdo.controller;

import com.tuananhdo.configure.PathConfiguration;
import com.tuananhdo.exception.EmailNotFoundException;
import com.tuananhdo.payload.ForgotPasswordDTO;
import com.tuananhdo.service.MailService;
import com.tuananhdo.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Controller
@AllArgsConstructor
public class ForgotPassowordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPassowordController.class);
    public static final String FIELD_NEW_PASSWORD = "newPassword";
    public static final String FIELD_EMAIL = "email";

    private final MailService mailService;
    private final UserAuthenticationService authenticationService;
    private final PathConfiguration pathConfiguration;

    @GetMapping("/forgot/password")
    public String forgotPassword(Model model) {
        ForgotPasswordDTO user = new ForgotPasswordDTO();
        model.addAttribute("user", user);
        return "authentication/forgot-password";
    }

    @PostMapping("/forgot/password")
    public String forgotPassword(@Valid @ModelAttribute("user") ForgotPasswordDTO user,
                                 BindingResult result,
                                 Model model) {
        if (result.hasFieldErrors(FIELD_EMAIL)) {
            model.addAttribute("email", user.getEmail());
            return "authentication/forgot-password";
        }
        try {
            String token = authenticationService.updateResetPasswordToken(user.getEmail());
            String linkReset = pathConfiguration.getRootPath() + "/reset/password?token=" + token;
            sendResetPasswordEmail(user, linkReset);
            model.addAttribute("message", "We sent your email for a link to reset your password");
        } catch (EmailNotFoundException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (MessagingException | UnsupportedEncodingException messagingException) {
            model.addAttribute("error", "error");
        }
        return "authentication/forgot-password";
    }

    private void sendResetPasswordEmail(ForgotPasswordDTO user, String linkReset) throws MessagingException, UnsupportedEncodingException {
        mailService.sendMailForgotPassword(user.getEmail(), linkReset);
    }


    @GetMapping("/reset/password")
    public String showResetPasswordForm(@ModelAttribute("user") ForgotPasswordDTO user,
                                        Model model) {
        String token = user.getToken();
        if (Objects.isNull(token) || !authenticationService.isResetPasswordTokenValid(token)) {
            model.addAttribute("message", "Token has expired or invalid token");
            return "authentication/forgot-password";
        }
        model.addAttribute("token", token);
        model.addAttribute("pageTitle", "Reset your password");
        return "authentication/reset-password";
    }

    @PostMapping("/reset/password")
    public String resetPassword(@Valid @ModelAttribute("user") ForgotPasswordDTO user,
                                BindingResult result,
                                Model model) {
        String token = user.getToken();
        if (result.hasFieldErrors(FIELD_NEW_PASSWORD)) {
            model.addAttribute("token", token);
            model.addAttribute("pageTitle", "Reset your password");
            LOGGER.error(result.getAllErrors().toString());
            return "authentication/reset-password";
        }
        authenticationService.updatePasswordAndRemoveToken(token, user);
        model.addAttribute("message", "New password changed successfully");
        return "authentication/login";
    }

}
