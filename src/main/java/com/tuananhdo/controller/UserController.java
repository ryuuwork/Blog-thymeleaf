package com.tuananhdo.controller;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailDuplicatedException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.paging.PaingAndSortingParam;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.service.UserAuthenticationService;
import com.tuananhdo.service.UserService;
import com.tuananhdo.utils.FileUploadUtil;
import com.tuananhdo.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final UserService userService;
    private final UserAuthenticationService authenticationService;

    @GetMapping("/admin/users")
    public String listAllUsers() {
        return "redirect:/admin/users/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/admin/users/page/{pageNumber}")
    public String listUserByPage(@PaingAndSortingParam(moduleURL = "/admin/users", listName = "users", pageTitle = "Management User") PagingAndSortingHelper helper,
                                 @PathVariable("pageNumber") int pageNumber, Model model) {
        userService.findAllUserByPage(pageNumber, helper);
        UserDTO loggedUser = userService.getLoggedUser();
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

    @GetMapping("/admin/user/update/{userId}")
    public String updateUser(@PathVariable("userId") Long userId, Model model) throws UserNotFoundException {
        List<Role> listRoles = userService.listRoles();
        UserDTO user = userService.findByUserId(userId);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Update User");
        return "admin/user/update-user";
    }

    @PostMapping("/admin/save/user")
    public String saveUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                           BindingResult result,
                           @RequestParam("image") MultipartFile multipartFile,
                           Model model,
                           RedirectAttributes redirectAttributes) throws IOException {
        if (existingUser(userDTO, result, model)) return "admin/user/create-user";
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

    @PostMapping("/admin/save/user/{userId}")
    public String updateUser(@PathVariable("userId") Long userId,
                             @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             @RequestParam("image") MultipartFile multipartFile,
                             Model model,
                             RedirectAttributes redirectAttributes) throws IOException, UserNotFoundException, EmailDuplicatedException {
        UserDTO getUser = userService.findByUserId(userDTO.getId());
        if (!userDTO.getEmail().equals(getUser.getEmail())) {
            if (existingUser(userDTO, result, model)) return "admin/user/update-user";
        }
        updateUserDetails(userId, userDTO, multipartFile);
        UserDTO updatedUser = userService.updateUser(userDTO);
        redirectAttributes.addFlashAttribute("message", "The user " + StringUtil.toLowerCase(updatedUser.getName()) + " has been updated successfully!");
        return redirectPageUrlForNameOfUser(userDTO);

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


    private boolean existingUser(@ModelAttribute("user") @Valid UserDTO userDTO, BindingResult result, Model model) {
        User existingUser = authenticationService.findByEmail(userDTO.getEmail());
        if (existingUser != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already a user with same email");
        }
        List<Role> listRoles = userService.listRoles();
        if (result.hasErrors()) {
            model.addAttribute("user", userDTO);
            model.addAttribute("listRoles", listRoles);
            return true;
        }
        return false;
    }

    @GetMapping("/admin/user/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId, RedirectAttributes redirectAttributes) throws IOException {
        userService.deleteUserById(userId);
        String folderImage = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, userId);
        FileUploadUtil.cleanDir(folderImage);
        redirectAttributes.addFlashAttribute("message", "The user ID: " + userId + " has been deleted successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/user/{userId}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("userId") Long userId,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) throws UserNotFoundException {
        UserDTO userDTO = userService.findByUserId(userId);
        userService.updateUserEnabledStatus(userId, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The user " + StringUtil.toLowerCase(userDTO.getName()) + " has been " + status);
        return redirectPageUrlForNameOfUser(userDTO);
    }
}
