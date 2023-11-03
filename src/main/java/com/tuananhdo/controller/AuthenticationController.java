package com.tuananhdo.controller;

import com.tuananhdo.configure.PathConfiguration;
import com.tuananhdo.entity.User;
import com.tuananhdo.payload.RegistrationDTO;
import com.tuananhdo.service.MailService;
import com.tuananhdo.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@AllArgsConstructor
public class AuthenticationController {
    private final MailService mailService;
    private final UserAuthenticationService authenticationService;
    private final PathConfiguration pathConfiguration;

    @GetMapping("/register")
    public String register(Model model) {
        RegistrationDTO userRegistration = new RegistrationDTO();
        model.addAttribute("userRegistration", userRegistration);
        return "authentication/register";
    }

    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("userRegistration") RegistrationDTO userRegistration,
                           BindingResult result,
                           Model model) {
        User existingUser = authenticationService.findByEmail(userRegistration.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already a user with same email");
        }
        if (result.hasErrors()) {
            model.addAttribute("userRegistration", userRegistration);
            return "authentication/register";
        }
        String randomCode = RandomString.make(64);
        userRegistration.setVerificationCode(randomCode);
        authenticationService.saveUserResigter(userRegistration);
        String token = "<a>" + pathConfiguration.getRootPath() + "/verify/email?token=" + userRegistration.getVerificationCode() + "</a>";
        mailService.sendVerificationMail(userRegistration, token);
        return "redirect:/register?success";
    }

    @GetMapping("/verify/email")
    public String verifyRegisterEmail(@RequestParam("token") String token,
                                      Model model) {
        boolean verifycationToken = authenticationService.getTokenVerification(token);
        String message = verifycationToken ? "Thank you for your email verification successfully" : "Email verification failed";
        model.addAttribute("message", message);
        return "authentication/login";
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal == null) {
            return "authentication/login";
        }
        return "redirect:/admin/posts";
    }
}
