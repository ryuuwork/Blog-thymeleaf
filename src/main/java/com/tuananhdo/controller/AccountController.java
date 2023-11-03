package com.tuananhdo.controller;

import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.service.UserService;
import com.tuananhdo.utils.FileUploadUtil;
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

import java.io.IOException;

@Controller
@AllArgsConstructor
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final UserService userService;

    @GetMapping("/admin/users/account/setting")
    public String viewAccountDetails(Model model) {
        UserDTO loggedUser = userService.getLoggedUser();
        model.addAttribute("loggedUser", loggedUser);
        return "/admin/user/account-setting";
    }

    @PostMapping("/admin/users/account/update")
    public String updateAccountDetails(@ModelAttribute("loggedUser") UserDTO userDTO, BindingResult bindingResult,
                                       @RequestParam("image") MultipartFile multipartFile,
                                       RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            LOGGER.info(bindingResult.getAllErrors().toString());
            return "/admin/user/account-setting";
        }
        try {
            if (!multipartFile.isEmpty()) {
                String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
                userDTO.setPhotos(fileName);
                UserDTO updated = userService.updateAccountDetails(userDTO);
                String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, updated.getId());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } else {
                userService.updateAccountDetails(userDTO);
            }
            redirectAttributes.addFlashAttribute("message", "The account :  " + StringUtil.toLowerCase(userDTO.getEmail()) + " has been updated.");
        } catch (PasswordValidationException | UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("exception", exception.getMessage());
        }
        return "redirect:/admin/users/account/setting";
    }
}
