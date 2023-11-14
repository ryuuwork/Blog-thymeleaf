package com.tuananhdo.service.impl;

import com.tuananhdo.entity.User;
import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.mapper.AccountMapper;
import com.tuananhdo.payload.AccountDTO;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;


    @Override
    public AccountDTO getLoggedAccount() {
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User user = userRepository.findByEmail(email);
        return accountMapper.mapToAccountDTO(user);
    }

    @Override
    public AccountDTO updateAccountDetails(AccountDTO accountDTO) throws UserNotFoundException, PasswordValidationException {
        User userInDB = userRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("Not found accountDTO with id : " + accountDTO.getId()));
        if (!accountDTO.getPassword().isEmpty()) {
            boolean isMatch = passwordEncoder.matches(accountDTO.getPassword(), userInDB.getPassword());
            if (isMatch) {
                userInDB.setPassword(passwordEncoder.encode(accountDTO.getNewPassword()));
            } else {
                throw new PasswordValidationException("Current password is incorrect");
            }
        }
        if (Objects.nonNull(accountDTO.getPhotos())) {
            userInDB.setPhotos(accountDTO.getPhotos());
        }
        userInDB.setName(accountDTO.getName());
        userInDB.setEnabled(true);
        userInDB.setEmail(accountDTO.getEmail());
        userInDB.setAddress(accountDTO.getAddress());
        userInDB.setPhoneNumber(accountDTO.getPhoneNumber());
        userRepository.save(userInDB);
        return accountMapper.mapToAccountDTO(userInDB);
    }
}
