package com.tuananhdo.controller;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailDuplicatedException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.paging.PaingAndSortingParam;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.service.AccountService;
import com.tuananhdo.service.UserService;
import com.tuananhdo.utils.FileUploadUtil;
import com.tuananhdo.utils.StringUtil;
import com.tuananhdo.validate.AddUserValidate;
import com.tuananhdo.validate.UpdateUserValidate;
import com.tuananhdo.validate.ValidateUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String DEFAULT_REDIRECT_URL = "redirect:/admin/users/page/1?sortField=name&sortDir=asc";

    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/admin/users")
    public String listAllUsers() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/admin/users/page/{pageNumber}")
    public String listUserByPage(@PaingAndSortingParam(moduleURL = "/admin/users", listName = "users", pageTitle = "Management User") PagingAndSortingHelper helper,
                                 @PathVariable("pageNumber") int pageNumber, Model model) {
        userService.findAllUserByPage(pageNumber, helper);
        UserDTO loggedUser = accountService.getLoggedAccount();
        model.addAttribute("loggedUser", loggedUser);
        return "admin/user/user-home";
    }

    @GetMapping("/admin/user/create")
    public String createUser(Model model) {
        List<Role> listRoles = userService.listRoles();
        UserDTO user = new UserDTO();
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "User Account Management");
        return "admin/user/create-user";
    }

    @PostMapping("/admin/save/user")
    public String saveUser(@Validated(AddUserValidate.class) @Valid @ModelAttribute("user") UserDTO userDTO,
                           BindingResult result,
                           @RequestParam("image") MultipartFile multipartFile,
                           Model model,
                           RedirectAttributes redirectAttributes) throws IOException {
        User existingUser = userService.getByEmail(userDTO.getEmail());
        if (existingUser != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already a user with same email");
        }
        if (result.hasErrors()) {
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("pageTitle", "User Account Management");
            model.addAttribute("listRoles", listRoles);
            LOGGER.error(result.getAllErrors().toString());
            return "admin/user/create-user";
        }
        if (!multipartFile.isEmpty()) {
            String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
            userDTO.setPhotos(fileName);
            UserDTO saveUser = userService.saveUser(userDTO);
            String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, saveUser.getId());
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            userService.saveUser(userDTO);
        }
        redirectAttributes.addFlashAttribute("message", "The user " + StringUtil.toLowerCase(userDTO.getName()) + " has been save successfully!");
        return redirectPageUrlForNameOfUser(userDTO);
    }

    @GetMapping("/admin/user/update/{userId}")
    public String updateUser(@PathVariable("userId") Long userId, Model model) throws UserNotFoundException {
        List<Role> listRoles = userService.listRoles();
        UserDTO user = userService.getUserById(userId);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Update User");
        return "admin/user/update-user";
    }

    @PostMapping("/admin/save/user/{userId}")
    public String updateUser(@PathVariable("userId") Long userId,
                             @Validated(UpdateUserValidate.class)
                             @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             @RequestParam("image") MultipartFile multipartFile,
                             Model model,
                             RedirectAttributes redirectAttributes) throws IOException, UserNotFoundException, EmailDuplicatedException {


        List<Role> listRoles = userService.listRoles();
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Update User");

        if (result.hasErrors()) {
            return "admin/user/update-user";
        }

        UserDTO userById = userService.getUserById(userId);
        String emailInForm = userDTO.getEmail();
        String emailInDB = userById.getEmail();
        boolean isValidEmailUnique = userService.isValidEmailUnique(emailInForm);

        if (!emailInDB.equals(emailInForm) && isValidEmailUnique) {
            result.rejectValue("email", null, "There is already a user with same email");
            return "admin/user/update-user";
        }

        if (!userDTO.getPassword().isEmpty() && ValidateUtil.isValidLengthPasswordAndEmptySpace(userDTO.getPassword())) {
            model.addAttribute("message", "Password should be at least 8 to 25 characters and not space empty");
            return "admin/user/update-user";
        }
        updateUserDetails(userId, userDTO, multipartFile);
        UserDTO updatedUser = userService.updateUser(userDTO);
        redirectAttributes.addFlashAttribute("message", "The user " + StringUtil.toLowerCase(updatedUser.getName()) + " has been updated successfully!");
        return DEFAULT_REDIRECT_URL;
    }

    private void updateUserDetails(Long userId, UserDTO userDTO, MultipartFile multipartFile) throws IOException, UserNotFoundException, EmailDuplicatedException {
        if (!multipartFile.isEmpty()) {
            String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
            userDTO.setPhotos(fileName);
            String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, userId);
            FileUploadUtil.saveFileAndCleanOldImage(multipartFile, fileName, uploadDir);
        }
    }

    private static String redirectPageUrlForNameOfUser(UserDTO userDTO) {
        String firstPathOfName = userDTO.getEmail().split("@")[0];
        return "redirect:/admin/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPathOfName;
    }

    @GetMapping("/admin/user/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId,
                             RedirectAttributes redirectAttributes) throws IOException, UserNotFoundException {
        userService.deleteUserById(userId);
        String folderImage = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, userId);
        FileUploadUtil.cleanDir(folderImage);
        redirectAttributes.addFlashAttribute("message", "The user ID: " + userId + " has been deleted successfully!");
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/admin/user/{userId}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("userId") Long userId,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) throws UserNotFoundException {
        UserDTO userDTO = userService.getUserById(userId);
        userService.updateUserEnabledStatus(userId, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The user " + StringUtil.toLowerCase(userDTO.getName()) + " has been " + status);
        return DEFAULT_REDIRECT_URL;
    }
}
