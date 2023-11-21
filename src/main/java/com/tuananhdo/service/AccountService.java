package com.tuananhdo.service;

import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AccountService {

    UserDTO getLoggedAccount();

    UserDTO updateAccountDetails(UserDTO accountDTO, MultipartFile multipartFile) throws UserNotFoundException, PasswordValidationException, IOException;
}
