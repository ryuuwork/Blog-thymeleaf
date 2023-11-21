package com.tuananhdo.repository;

import com.tuananhdo.entity.User;
import com.tuananhdo.utils.AuthenticationType;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback(value = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTests.class);
    private static final String PREFIX_NUMBER = "09";
    public static final String SUFFIX_EMAIL = "_test@gmail.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager manager;

    @AfterEach
    public void cleanUp() {
        List<User> removeUsers = userRepository.findByEmailEndingWith(SUFFIX_EMAIL);
        userRepository.deleteAll(removeUsers);
    }

    public static String random(int length) {
        return RandomString.make(length);
    }

    private static String genetateRandomPhoneNumber() {
        Random random = new Random();
        return PREFIX_NUMBER + random.ints(8, 0, 10)
                .mapToObj(Long::toString)
                .collect(Collectors.joining());
    }

    private static String encodedPassword(String rawPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }

    private static User createDefaultUser() {
        String email = random(15) + SUFFIX_EMAIL;
        String phoneNumber = genetateRandomPhoneNumber();
        String password = encodedPassword(random(13));
        return User.builder()
                .name("UserOne")
                .address("Tokyo")
                .photos("user.jpg")
                .password(password)
                .phoneNumber(phoneNumber)
                .authenticationType(AuthenticationType.DATABASE)
                .email(email)
                .build();
    }

    @Test
    void getUserById() {
        User user = createDefaultUser();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertTrue(foundUser.isPresent());
        User userOptinal = foundUser.orElse(null);
        assertNotNull(userOptinal);
    }

    @Test
    void findByEmail() {
        User user = createDefaultUser();
        userRepository.save(user);

        User foundUser = userRepository.findByEmail(user.getEmail());
        assertNotNull(foundUser);
        Assertions.assertEquals(user.getName(), foundUser.getName());
        assertNotNull(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void updateAuthenticationType() {
        User user = createDefaultUser();
        userRepository.save(user);
        userRepository.updateAuthenticationType(user.getId(), AuthenticationType.FACEBOOK);
        manager.refresh(user);
        User updatedUser = userRepository.findByEmail(user.getEmail());
        Assertions.assertEquals(AuthenticationType.FACEBOOK, updatedUser.getAuthenticationType());
        Assertions.assertEquals(user.getName(), updatedUser.getName());
    }

    @Test
    void findByResetPasswordToken() {
        String token = random(20);
        User user = createDefaultUser();
        user.setResetPasswordToken(token);
        userRepository.save(user);
        User result = userRepository.findByResetPasswordToken(user.getResetPasswordToken());

        assertNotNull(result);
        assertEquals(user.getResetPasswordToken(), result.getResetPasswordToken());
    }

    @Test
    void enableUserAndRemoveToken() {
        String token = random(10);
        User user = createDefaultUser();
        user.setEnabled(false);
        user.setAccountNonLocked(false);
        user.setVerificationCode(token);
        userRepository.save(user);

        userRepository.enableUserAndRemoveToken(user.getId());
        manager.refresh(user);

        User result = userRepository.findByEmail(user.getEmail());

        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonLocked());
        assertNull(result.getVerificationCode());
    }

    @Test
    void updateEnabledStatus() {
        User user = createDefaultUser();
        userRepository.save(user);

        System.out.println("Enabled:" + user.isEnabled());

        userRepository.updateEnabledStatus(user.getId(), true);

        User users = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(users);
        manager.refresh(user);
        System.out.println("Enabled:" + users.isEnabled());
        assertThat(users.isEnabled()).isTrue();
    }

    @Test
    void findByVerificationCode() {
        User user = createDefaultUser();
        String token = RandomString.make(30);
        user.setVerificationCode(token);
        userRepository.save(user);
        User result = userRepository.findByVerificationCode(user.getVerificationCode());

        assertNotNull(result.getVerificationCode());
        assertEquals(user.getVerificationCode(), result.getVerificationCode());
    }

    @Test
    void findAll() {
        User userOne = createDefaultUser();
        User userTwo = createDefaultUser();
        List<User> users = Arrays.asList(userOne, userTwo);
        userRepository.saveAll(users);

        List<User> result = userRepository.findAll();
        assertThat(result)
                .isNotEmpty()
                .containsAll(users);
    }

    @Test
    void findAllById() {
        User userOne = createDefaultUser();
        User userTwo = createDefaultUser();

        List<User> users = Arrays.asList(userOne, userTwo);
        userRepository.saveAll(users);

        List<User> result = userRepository.findAllById(Arrays.asList(userOne.getId(), userTwo.getId()));
        assertThat(result)
                .isNotEmpty()
                .containsAll(users);
    }

    @Test
    void findAllByKeyWord() {
        User userOne = createDefaultUser();
        User userTwo = User.builder()
                .name("Davis")
                .photos("davis.jpg")
                .address("Franch")
                .password("")
                .phoneNumber("")
                .email(random(15) + SUFFIX_EMAIL)
                .authenticationType(AuthenticationType.DATABASE)
                .build();
        List<User> users = Arrays.asList(userOne, userTwo);
        userRepository.saveAll(users);

        String keyword = "UserOne";
        Page<User> result = userRepository.findAllByKeyWord(PageRequest.of(0, 5), keyword);
        assertEquals(userOne.getName(), result.getContent().get(0).getName());
    }

    @Test
    void updateFailedAttempt() {
        User user = createDefaultUser();
        user.setFailedAttempt(0);
        userRepository.save(user);
        int newFailedAttempt = 1;
        userRepository.updateFailedAttempt(newFailedAttempt, user.getEmail());
        manager.refresh(user);
        User updatedUser = userRepository.getUserById(user.getId());
        Assertions.assertEquals(user.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(newFailedAttempt, updatedUser.getFailedAttempt());
    }

    @Test
    void findAllAccountExpired() {
        User user = createDefaultUser();
        user.setAccountNonLocked(false);
        user.setLockTime(LocalDateTime.now().minusMinutes(10));
        userRepository.save(user);

        List<User> expiredUser = userRepository.findAllAccountExpired(LocalDateTime.now());
        assertThat(expiredUser)
                .isNotEmpty()
                .contains(user);
    }

    @Test
    void findAllTokenResetPasswordExpired() {
        String token = random(20);
        User user = createDefaultUser();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpirationTime(LocalDateTime.now().minusMinutes(15));
        userRepository.save(user);
        List<User> result = userRepository.findAllTokenResetPasswordExpired(LocalDateTime.now());
        assertThat(result)
                .isNotEmpty()
                .contains(user);
        assertThat(user.getResetPasswordToken())
                .isNotNull().isEqualTo(token);
    }
}