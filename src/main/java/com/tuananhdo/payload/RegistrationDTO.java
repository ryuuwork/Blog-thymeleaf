package com.tuananhdo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

    private Long id;
    @NotBlank(message = "{register.firstname.not.blank}")
    private String firstName;
    @NotBlank(message = "{register.lastname.not.blank}")
    private String lastName;
    @NotBlank(message = "{register.email.not.blank}")
    @Email
    private String email;
    @NotBlank(message = "{register.password.not.blank}")
    @Size(min = 8,max = 20,message = "{register.password.size}")
    private String password;
    private String verificationCode;
}
