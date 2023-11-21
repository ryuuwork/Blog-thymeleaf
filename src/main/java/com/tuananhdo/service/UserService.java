package com.tuananhdo.service;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailDuplicatedException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.payload.UserDTO;

import java.util.List;

public interface UserService {

    void findAllUserByPage(int pageNumber, PagingAndSortingHelper helper);

    List<Role> listRoles();

    UserDTO saveUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO user) throws UserNotFoundException, EmailDuplicatedException;

    boolean isValidEmailUnique(String email);
    void deleteUserById(Long userId) throws UserNotFoundException;

    void deleteSelectedUsers(List<Long> userIds);

    UserDTO getUserById(Long id) throws UserNotFoundException;

    void updateUserEnabledStatus(Long userId, boolean enabled);

    User getByEmail(String email);

    void increaseFailedAttempt(User user);

    void lock(User user);

    void unlock(User user);

    void resetFailedAttempts(String email);

    List<User> getAllExpiredLockedAccounts();

    List<User> getAllTokenResetPasswordExpired();
    void removeTokenExpired(User user);
}
