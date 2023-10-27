package com.tuananhdo.service;

import com.tuananhdo.payload.RegistrationDTO;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.EmailNotFoundException;
import com.tuananhdo.utils.AuthenticationType;

public interface UserAuthenticationService {

    void saveUserResigter(RegistrationDTO registrationDTO);

    User findByEmail(String email);

    void updateAuthentication(User user, AuthenticationType type);

    void createUserOAuthLogin(String name, String email);

    String updateResetPasswordToken(String email) throws EmailNotFoundException;

    boolean isResetPasswordTokenValid(String token);

    void updatePasswordAfterReset(String token, String password);

    boolean getTokenVerification(String token);
}
