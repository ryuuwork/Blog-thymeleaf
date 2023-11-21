package com.tuananhdo.service.impl;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.mapper.UserMapper;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.repository.RoleRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.schedule.UserAccountScheduleJob;
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
    public boolean isValidEmailUnique(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
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
    public void unlock(User user) {
        if (Objects.nonNull(user.getLockTime()) && isUnlockTimePassed(user)) {
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
    public List<User> getAllExpiredLockedAccounts() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return userRepository.findAllAccountExpired(currentDateTime);
    }

    @Override
    public List<User> getAllTokenResetPasswordExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeExpired = now.minusMinutes(15);
        return userRepository.findAllTokenResetPasswordExpired(timeExpired);
    }

    @Override
    public void removeTokenExpired(User user) {
        if (Objects.nonNull(user.getResetPasswordTokenExpirationTime())) {
            user.setResetPasswordTokenExpirationTime(null);
            user.setResetPasswordToken(null);
            userRepository.save(user);
        }
    }
    @Override
    public void findAllUserByPage(int pageNumber, PagingAndSortingHelper helper) {
        Page<UserDTO> pageUserDTO = helper.sortAndPagingOrSearchAllPage(pageNumber, USERS_SIZE_PAGE, userRepository, UserMapper::mapToUserDTO);
        helper.updateModelAttributes(pageNumber, pageUserDTO);
    }


    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = new User();
        user.coppyAllFields(userDTO);
        user.setPassword(encodedPassword(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    private String encodedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public UserDTO updateUser(UserDTO userInForm) throws UserNotFoundException {
        User userInDB = userRepository.findById(userInForm.getId()).orElseThrow(() -> new UserNotFoundException("User ID " + userInForm.getId() + " not found"));
        String password = userInForm.getPassword().isEmpty() ? userInDB.getPassword() : encodedPassword(userInForm.getPassword());
        userInDB.setPassword(password);
        userInDB.setRoles(userInDB.getRoles());
        userInDB.coppyFields(userInForm);
        User updated = userRepository.save(userInDB);
        return UserMapper.mapToUserDTO(updated);
    }

    @Override
    public UserDTO getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User ID " + id + " not found"));
    }

    @Override
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }

    @Override
    public void deleteUserById(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User ID " + userId + " not found"));
        userRepository.delete(user);
    }

    @Override
    public void deleteSelectedUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        userRepository.deleteAll(users);
    }
}
