package com.tuananhdo.service.impl;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.mapper.UserMapper;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.AccountService;
import com.tuananhdo.utils.FileUploadUtil;
import com.tuananhdo.validate.ValidateUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getLoggedAccount() {
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User user = userRepository.findByEmail(email);
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateAccountDetails(UserDTO accountDetails, MultipartFile multipartFile) throws UserNotFoundException, PasswordValidationException, IOException {
        User userInDB = userRepository.findById(accountDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("Not found account with: " + accountDetails.getId()));

        validateCurrentPassword(accountDetails);

        validateIncorrectAndLengthPassword(accountDetails, userInDB);

        validatePasswordAndFile(accountDetails, multipartFile);

        validateNewPassword(accountDetails);

        handleFileAndCleanFile(accountDetails, multipartFile);

        userInDB.coppyFields(accountDetails);
        userRepository.save(userInDB);
        return UserMapper.mapToUserDTO(userInDB);
    }

    private void validateCurrentPassword(UserDTO accountDetails) throws PasswordValidationException {
        if (StringUtils.isBlank(accountDetails.getPassword())) {
            throw new PasswordValidationException("Enter your current password for security verification");
        }
    }

    private void validatePasswordAndFile(UserDTO accountDetails, MultipartFile multipartFile) throws PasswordValidationException {
        if (multipartFile.isEmpty() && StringUtils.isNotBlank(accountDetails.getPassword()) && accountDetails.getNewPassword().isEmpty()) {
            throw new PasswordValidationException("Enter a new password 8-25 characters if you want to change new password");
        }
    }

    private void validateNewPassword(UserDTO accountDetails) throws PasswordValidationException {
        String newPassword = accountDetails.getNewPassword();
        String password = accountDetails.getPassword();

        if (!newPassword.isEmpty() && !password.isEmpty() &&
                ValidateUtil.isValidLengthPasswordAndEmptySpace(newPassword) &&
                ValidateUtil.isValidLengthPasswordAndEmptySpace(password)) {
            throw new PasswordValidationException("Current password and new password should be at least 8 to 25 characters and not space empty");
        }
        if (!newPassword.isEmpty() && password.isEmpty()) {
            throw new PasswordValidationException("Enter current password for security verification before save new validatePassword");
        }

        if (!newPassword.isEmpty() && ValidateUtil.isValidLengthPasswordAndEmptySpace(newPassword)) {
            throw new PasswordValidationException("New Password should be at least 8 to 25 characters and not space empty");
        }
    }

    private void validateIncorrectAndLengthPassword(UserDTO accountDetails, User userInDB) throws PasswordValidationException {
        boolean validatePassword = StringUtils.isBlank(accountDetails.getPassword());
        if (!validatePassword && ValidateUtil.isValidLengthPasswordAndEmptySpace(accountDetails.getPassword())) {
            throw new PasswordValidationException("Current Password should be at least 8 to 25 characters");
        }
        if (!validatePassword) {
            boolean passwordMatch = isCurrentPasswordMatch(accountDetails.getPassword(), userInDB.getPassword());
            if (passwordMatch) {
                String passwordChange = accountDetails.getNewPassword().isEmpty() ? accountDetails.getPassword() : accountDetails.getNewPassword();
                userInDB.setPassword(encodePassword(passwordChange));
            } else {
                throw new PasswordValidationException("Current password is incorrect");
            }
        }
    }

    private void handleFileAndCleanFile(UserDTO accountDetails, MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty() && StringUtils.isNotBlank(accountDetails.getPassword())) {
            String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
            accountDetails.setPhotos(fileName);
            String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.USER_PHOTOS, accountDetails.getId());
            FileUploadUtil.saveFileAndCleanOldImage(multipartFile, fileName, uploadDir);
        }
    }

    private boolean isCurrentPasswordMatch(String passwordInForm, String passwordInDB) {
        return passwordEncoder.matches(passwordInForm, passwordInDB);
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
