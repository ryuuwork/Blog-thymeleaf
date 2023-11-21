package com.tuananhdo.controller;

import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.service.AccountService;
import com.tuananhdo.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private static final String ACCOUNT_SETTING_VIEW = "/admin/user/account-setting";

    private final AccountService accountService;

    @GetMapping("/admin/users/account/setting")
    public String viewAccountDetails(Model model) {
        UserDTO loggedUser = accountService.getLoggedAccount();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("pageTitle", "Account Setting");
        return "/admin/user/account-setting";
    }

    @PostMapping("/admin/users/account/update")
    public String updateAccountDetails(@Valid @ModelAttribute("loggedUser") UserDTO userDTO,
                                       BindingResult bindingResult,
                                       @RequestParam("image") MultipartFile multipartFile,
                                       Model model,
                                       RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            LOGGER.error(bindingResult.getAllErrors().toString());
            model.addAttribute("pageTitle", "Account Setting");
            return "/admin/user/account-setting";
        }
        try {
            accountService.updateAccountDetails(userDTO, multipartFile);
            redirectAttributes.addFlashAttribute("message", "The account :  " + StringUtil.toLowerCase(userDTO.getEmail()) + " has been updated.");
        } catch (PasswordValidationException | UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        }
        return "redirect:/admin/users/account/setting";
    }
}
