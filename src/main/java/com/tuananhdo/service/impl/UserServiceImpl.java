package com.tuananhdo.service.impl;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.mapper.UserMapper;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.repository.RoleRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.schedule.UserAccountScheduleJob;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final int USERS_SIZE_PAGE = 6;
    public static final int NUMBER_MINUTES = 1;
    public static final int INCREASE_FAILED_ATTEMPT = 1;
    public static final int RESET_FAILED_ATTEMPT = 0;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void increaseFailedAttempt(User user) {
        int newFailedAttempts = user.getFailedAttempt() + INCREASE_FAILED_ATTEMPT;
        userRepository.updateFailedAttempt(newFailedAttempts, user.getEmail());
    }

    @Override
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempt(RESET_FAILED_ATTEMPT, email);
    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(LocalDateTime.now().plusMinutes(NUMBER_MINUTES));
        userRepository.save(user);
        UserAccountScheduleJob.executorService.schedule(()
                -> unlock(user), NUMBER_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public List<User> getAllExpiredLockedAccounts() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return userRepository.findAllAccountExpired(currentDateTime);
    }

    @Override
    public void unlock(User user) {
        if (Objects.isNull(user.getLockTime()) || isUnlockTimePassed(user)) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(RESET_FAILED_ATTEMPT);
            userRepository.save(user);
        }
    }

    private static boolean isUnlockTimePassed(User user) {
        return user.getLockTime().isBefore(LocalDateTime.now());
    }

    @Override
    public void findAllUserByPage(int pageNumber, PagingAndSortingHelper helper) {
        Page<UserDTO> pageUserDTO = helper.sortAndPagingOrSearchAllPage(pageNumber, USERS_SIZE_PAGE, userRepository, UserMapper::mapToUserDTO);
        helper.updateModelAttributes(pageNumber, pageUserDTO);
    }

    @Override
    public UserDTO getLoggedUser() {
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User user = userRepository.findByEmail(email);
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO updateAccountDetails(UserDTO accountDTO) throws UserNotFoundException, PasswordValidationException {
        User userInDB = userRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("Not found user with id : " + accountDTO.getId()));
        if (!accountDTO.getPassword().isEmpty()) {
            boolean isMatch = passwordEncoder.matches(accountDTO.getPassword(), userInDB.getPassword());
            if (isMatch) {
                userInDB.setPassword(accountDTO.getNewPassword());
                encodedPassword(userInDB);
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
        return UserMapper.mapToUserDTO(userInDB);
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.maptoUser(userDTO);
        user.setEnabled(true);
        encodedPassword(user);
        User saveUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(saveUser);
    }

    private void encodedPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }


    @Override
    public UserDTO updateUser(UserDTO userInForm) throws UserNotFoundException {
        User userInDB = userRepository.findById(userInForm.getId()).orElseThrow(()
                -> new UserNotFoundException("User ID " + userInForm.getId() + " not found"));
        User user = UserMapper.maptoUser(userInForm);
        if (user.getPassword().isEmpty()) {
            user.setPassword(userInDB.getPassword());
        } else {
            encodedPassword(user);
        }
        user.setEnabled(userInDB.isEnabled());
        user.setAccountNonLocked(userInDB.isAccountNonLocked());
        userRepository.save(user);
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO findByUserId(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User ID " + id + " not found"));
    }

    @Override
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

}
