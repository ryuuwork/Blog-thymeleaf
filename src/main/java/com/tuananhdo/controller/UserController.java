package com.tuananhdo.controller;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailDuplicatedException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.service.FileUploadService;
import com.tuananhdo.service.UserAuthenticationService;
import com.tuananhdo.service.UserService;
import com.tuananhdo.service.impl.FileUploadServiceImpl;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.tuananhdo.service.impl.FileUploadServiceImpl.USER_PHOTOS;

@Controller
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    private final UserService userService;
    private final UserAuthenticationService authenticationService;
    private final FileUploadService fileUploadService;


    @GetMapping("/admin/users")
    public String listAllUsers(Model model) {
        return listUserByPage(1, model,null);
    }

    @GetMapping("/admin/users/page/{pageNumber}")
    public String listUserByPage(@PathVariable("pageNumber") int pageNumber,
                                 Model model,
                                 @Param("keyword") String keyword) {
        Page<UserDTO> users = userService.findAllUserByPage(pageNumber, keyword);
        int pageSize = 4;
        int startCount = (pageNumber - 1) * pageSize + 1;
        long endCount = Math.min(startCount + pageSize - 1, users.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("users", users);
        model.addAttribute("users", users);
        model.addAttribute("userTitle", "User Account Management");
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", users.getTotalElements());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/user/user-home";
    }

    @GetMapping("/admin/user/create")
    public String createUser(Model model) {
        List<Role> listRoles = userService.listRoles();
        UserDTO user = new UserDTO();
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("user", user);
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
        userService.updateUser(userDTO);
        redirectAttributes.addFlashAttribute("message", "The user ID: " + userId + " has been updated successfully!");
        return "redirect:/admin/users";
    }

    private void updateUserDetails(Long userId, UserDTO userDTO, MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = FileUploadServiceImpl.getFileName(multipartFile);
            userDTO.setPhotos(fileName);
            String uploadDir = USER_PHOTOS + userDTO.getId();
            FileUploadServiceImpl.saveFile(multipartFile, fileName, uploadDir);
        } else {
            fileUploadService.cleanUploadDir(userId);
        }
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


    @PostMapping("/admin/save/user")
    public String saveUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result,
                           @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
        if (existingUser(userDTO, result, model)) return "admin/user/create-user";
        if (!multipartFile.isEmpty()) {
            fileUploadService.saveUserWithFile(userDTO, multipartFile);
        } else {
            userService.saveUser(userDTO);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/user/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId);
        redirectAttributes.addFlashAttribute("message", "The user ID: " + userId + " has been deleted successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/user/{userId}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("userId") Long userId,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(userId, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The user ID : " + userId + " has been " + status);
        return "redirect:/admin/users";
    }
}
