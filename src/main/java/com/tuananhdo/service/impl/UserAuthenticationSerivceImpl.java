package com.tuananhdo.service.impl;

import com.tuananhdo.payload.RegistrationDTO;
import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailNotFoundException;
import com.tuananhdo.repository.RoleRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.service.UserAuthenticationService;
import com.tuananhdo.utils.AuthenticationType;
import com.tuananhdo.utils.ErrorMessageUtil;
import com.tuananhdo.utils.ROLE;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserAuthenticationSerivceImpl implements UserAuthenticationService {
    public static final int TOKEN_EXPIRATION_HOURS = 2;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserResigter(RegistrationDTO registrationDTO) {
        User user = new User();
        user.setName(registrationDTO.getFirstName() + registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setAuthenticationType(AuthenticationType.DATABASE);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setVerificationCode(registrationDTO.getVerificationCode());
        Role role = roleRepository.findByName(ROLE.ROLE_GUEST.name());
        user.setRoles(Stream.of(role).collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void updateAuthentication(User user, AuthenticationType type) {
        if (!user.getAuthenticationType().equals(type)) {
            userRepository.updateAuthenticationType(user.getId(), type);
        }
    }

    @Override
    public void createUserOAuthLogin(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAuthenticationType(AuthenticationType.GOOGLE);
        user.setPassword("");
        userRepository.save(user);
    }

    @Override
    public String updateResetPasswordToken(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email);
        if (Objects.nonNull(user)) {
            String token = RandomString.make(30);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpirationTime(LocalDateTime.now());
            userRepository.save(user);
            return token;
        } else {
            throw new EmailNotFoundException(ErrorMessageUtil.EMAIL_NOT_FOUND_ERROR);
        }

    }

    @Override
    public boolean isResetPasswordTokenValid(String token) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user != null && user.getResetPasswordTokenExpirationTime() != null) {
            LocalDateTime nowTime = LocalDateTime.now();
            LocalDateTime tokenExpirationTime = user.getResetPasswordTokenExpirationTime();
            Duration duration = Duration.between(tokenExpirationTime, nowTime);
            return duration.toHours() <= TOKEN_EXPIRATION_HOURS;
        }
        return false;
    }

    @Override
    public void updatePasswordAfterReset(String token, String password) {
        User user = userRepository.findByResetPasswordToken(token);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(ErrorMessageUtil.NO_TOKEN_FOUND);
        }
        encodePassword(password, user);
    }

    private void encodePassword(String password, User user) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpirationTime(null);
        userRepository.save(user);
    }

    @Override
    public boolean getTokenVerification(String token) {
        User user = userRepository.findByVerificationCode(token);
        if (Objects.isNull(user) || user.isEnabled()) {
            return false;
        } else {
            userRepository.enableUser(user.getId());
            return true;
        }
    }
}
