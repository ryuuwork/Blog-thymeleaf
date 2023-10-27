package com.tuananhdo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

    private Long id;
    @NotEmpty(message = "Can not leave the first name blank")
    private String firstName;
    @NotEmpty(message = "Can not leave the last name blank")
    private String lastName;
    @NotEmpty(message = "Can not leave the email blank")
    @Email
    private String email;
    @NotEmpty(message = "Can not leave the password blank")
    private String password;
    private String verificationCode;
}
