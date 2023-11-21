package com.tuananhdo.service;

import com.tuananhdo.entity.User;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.service.impl.UserServiceImpl;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testDeleteUserById() {
        User user = User.builder()
                .email("usertest@gmail.com")
                .enabled(true)
                .name("test")
                .address("Tokyo")
                .password("password")
                .build();
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        Optional<User> foundUser = userRepository.findById(2L);
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertAll(() -> userService.deleteUserById(2L));
    }

    @Test
    public void testDeleteUserById_AndThrowUserNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(2L));
    }

    @Test
    public void testUpdateUserEnableStatus() {
        Long userId = 10L;
        boolean enableStatus = true;
        userService.updateUserEnabledStatus(userId, enableStatus);
        verify(userRepository, times(1)).updateEnabledStatus(userId, enableStatus);
    }

    @Test
    public void testDeleteSelectedUsers() {
        User userTest = User.builder()
                .id(1L)
                .email("usertest@gmail.com")
                .enabled(true)
                .name("test")
                .address("Tokyo")
                .password("password")
                .build();

        User userGuest = User.builder()
                .id(2L)
                .email("userguest@gmail.com")
                .enabled(true)
                .name("guest")
                .address("America")
                .password("password")
                .build();

        List<Long> userIds = Arrays.asList(userGuest.getId(), userTest.getId());
        List<User> users = Arrays.asList(userGuest, userTest);

        when(userRepository.findAllById(ArgumentMatchers.eq(userIds))).thenReturn(users);

        userService.deleteSelectedUsers(userIds);
        verify(userRepository, times(1)).deleteAll(users);
    }

    @Test
    public void testGetUserById_WhenUserExists_AndReturnUserDTO() throws UserNotFoundException {
        Long userId = 10L;
        User existingUser = User.builder()
                .email("userguest@gmail.com")
                .enabled(true)
                .name("guest")
                .address("America")
                .password("password")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDTO userDTO = userService.getUserById(userId);
        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(existingUser.getId(), userDTO.getId());
        Assertions.assertEquals(existingUser.getName(), userDTO.getName());
        Assertions.assertEquals(existingUser.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(existingUser.getPassword(), userDTO.getPassword());
    }

    @Test
    public void testGetUserById_WhenUserNotExists() {
        Long userId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void testUpdateUser_AndReturnUserDTO() throws UserNotFoundException {
        UserDTO userInForm = UserDTO.builder()
                .id(2L)
                .email("Joe@gmail.com")
                .enabled(true)
                .name("Joe")
                .photos("user-2.jpg")
                .address("Sudney")
                .password("Joe")
                .build();

        User userInDB = User.builder()
                .id(2L)
                .email("john@gmail.com")
                .enabled(true)
                .name("John")
                .photos("user-2.jpg")
                .address("Africa")
                .password("John")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(userInDB));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(userInDB);

        UserDTO updatedUser = userService.updateUser(userInForm);
        Assertions.assertEquals(userInForm.getName(), updatedUser.getName());

        Assertions.assertEquals(userInForm.getId(), updatedUser.getId());
        Assertions.assertEquals(userInForm.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(userInForm.getName(), updatedUser.getName());
        Assertions.assertEquals(userInForm.getAddress(), updatedUser.getAddress());
        verify(passwordEncoder).encode(userInForm.getPassword());
    }

    @Test
    public void testCreateUser_AndReturnUserDTO() {
        UserDTO userInForm = UserDTO.builder()
                .email("Joe@gmail.com")
                .enabled(true)
                .accountNonLocked(true)
                .name("Joe")
                .photos("user-2.jpg")
                .address("Sudney")
                .password("Joe")
                .build();

        User user = User.builder()
                .email("Joe@gmail.com")
                .enabled(true)
                .accountNonLocked(true)
                .name("Joe")
                .photos("user-2.jpg")
                .address("Sudney")
                .password("Joe")
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        UserDTO saveForUser = userService.saveUser(userInForm);

        Assertions.assertTrue(userInForm.isEnabled());
        Assertions.assertTrue(userInForm.isAccountNonLocked());
        Assertions.assertEquals(userInForm.getId(), saveForUser.getId());
        Assertions.assertEquals(userInForm.getName(), saveForUser.getName());
        Assertions.assertEquals(userInForm.getEmail(), saveForUser.getEmail());
        Assertions.assertEquals(userInForm.getName(), saveForUser.getName());
        Assertions.assertEquals(userInForm.getAddress(), saveForUser.getAddress());
        verify(passwordEncoder).encode(userInForm.getPassword());
    }

    @Test
    public void testGetAllExpiredLockedAccounts() {
        User whick = User.builder()
                .name("whick")
                .accountNonLocked(false)
                .lockTime(LocalDateTime.now().minusMinutes(15))
                .build();

        User john = User.builder()
                .name("john")
                .accountNonLocked(true)
                .lockTime(null)
                .build();

        List<User> users = Arrays.asList(whick, john);

        when(userRepository.findAllAccountExpired(Mockito.any(LocalDateTime.class))).thenReturn(users);

        List<User> result = userService.getAllExpiredLockedAccounts();

        long count = result.stream().filter(user -> !user.isAccountNonLocked() && user.getLockTime().isBefore(LocalDateTime.now())).count();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testUnlockUser() {
        LocalDateTime pastLockTime = LocalDateTime.now().minusMinutes(15);
        User unLockUser = User.builder()
                .name("guest")
                .accountNonLocked(false)
                .lockTime(pastLockTime)
                .failedAttempt(21)
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(unLockUser);
        userService.unlock(unLockUser);

        Assertions.assertTrue(unLockUser.isAccountNonLocked());
        Assertions.assertNull(unLockUser.getLockTime());
        Assertions.assertEquals(0, unLockUser.getFailedAttempt());
        verify(userRepository).save(unLockUser);
    }

    @Test
    public void testLockUserAfterEnterWrongPassword_MoreThanAllowedNumberOfTimes() {
        User lockUser = User.builder()
                .name("guest")
                .accountNonLocked(true)
                .lockTime(null)
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(lockUser);
        userService.lock(lockUser);

        Assertions.assertFalse(lockUser.isAccountNonLocked());
        Assertions.assertNotNull(lockUser.getLockTime());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testGetAllTokenResetPasswordExpired() {
        String random = RandomString.make(15);
        LocalDateTime timeExpired = LocalDateTime.now().minusMinutes(15);
        User userExpiredOne = User.builder()
                .name("guest")
                .resetPasswordTokenExpirationTime(LocalDateTime.now().minusMinutes(20))
                .resetPasswordToken(random)
                .build();

        User userExpiredTwo = User.builder()
                .name("sale")
                .resetPasswordTokenExpirationTime(LocalDateTime.now().minusMinutes(20))
                .resetPasswordToken(random)
                .build();
        List<User> users = Arrays.asList(userExpiredOne,userExpiredTwo);

        when(userRepository.findAllTokenResetPasswordExpired(any(LocalDateTime.class))).thenReturn(users);

        List<User> result = userService.getAllTokenResetPasswordExpired();

        Assertions.assertTrue(result.containsAll(users));
        users.forEach(user -> Assertions.assertTrue(user.getResetPasswordTokenExpirationTime().isBefore(timeExpired)));
    }

    @Test
    public void testResetFailedAttempts() {
        String email = "guest@gmail.com";
        int resetFailedAttempt = 0;
        userService.resetFailedAttempts(email);
        verify(userRepository, times(1)).updateFailedAttempt(eq(resetFailedAttempt), eq(email));
    }

    @Test
    public void testIsValidEmailUnique() {
        String email = "unique@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(null);
        boolean result = userService.isValidEmailUnique(email);
        Assertions.assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testIsValidEmailNotUnique() {
        String email = "unique@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(new User());
        boolean result = userService.isValidEmailUnique(email);
        Assertions.assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }
}
