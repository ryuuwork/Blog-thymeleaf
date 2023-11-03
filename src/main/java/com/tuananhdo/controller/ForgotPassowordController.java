package com.tuananhdo.controller;

import com.tuananhdo.configure.PathConfiguration;
import com.tuananhdo.payload.ForgotPasswordDTO;
import com.tuananhdo.exception.EmailNotFoundException;
import com.tuananhdo.service.MailService;
import com.tuananhdo.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
@AllArgsConstructor
public class ForgotPassowordController {

    private final MailService mailService;
    private final UserAuthenticationService authenticationService;
    private final PathConfiguration pathConfiguration;

    @GetMapping("/forgot/password")
    public String forgotPassword(Model model) {
        ForgotPasswordDTO forgotPassword = new ForgotPasswordDTO();
        model.addAttribute("forgotPassword", forgotPassword);
        return "authentication/forgot-password";
    }

    @PostMapping("/forgot/password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPassword") ForgotPasswordDTO forgotPassword,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("email", forgotPassword.getEmail());
        } else {
            try {
                String token = authenticationService.updateResetPasswordToken(forgotPassword.getEmail());
                String linkReset = pathConfiguration.getRootPath() + "/reset/password?token=" + token;
                sendResetPasswordEmail(forgotPassword, linkReset);
                model.addAttribute("message", "We sent your email for a link to reset your password");
            } catch (EmailNotFoundException exception) {
                model.addAttribute("error", exception.getMessage());
            } catch (MessagingException | UnsupportedEncodingException messagingException) {
                messagingException.printStackTrace();
            }
        }
        return "authentication/forgot-password";
    }

    private void sendResetPasswordEmail(ForgotPasswordDTO forgotPassword, String linkReset) throws MessagingException, UnsupportedEncodingException {
        mailService.sendMailForgotPassword(forgotPassword.getEmail(), linkReset);
    }


    @GetMapping("/reset/password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        boolean isTokenValid = authenticationService.isResetPasswordTokenValid(token);
        if (isTokenValid) {
            model.addAttribute("token", token);
            model.addAttribute("pageTitle", "Reset your password");
            return "authentication/reset-password";
        } else {
            model.addAttribute("message", "Token has expired or invalid");
            return "authentication/forgot-password";
        }
    }

    @PostMapping("/reset/password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password, Model model) {
        authenticationService.updatePasswordAfterReset(token, password);
        model.addAttribute("message", "New password set successfully");
        return "authentication/login";
    }

}
