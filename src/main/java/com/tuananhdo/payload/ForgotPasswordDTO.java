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
public class ForgotPasswordDTO {
    @Email(message = "{forgot.password.valid.email}")
    @NotBlank(message = "{forgot.password.email.not.blank}")
    private String email;
    @NotBlank(message = "{user.password.new.not.blank}")
    @Size(min = 8,max = 20,message = "{user.password.new.size}")
    private String newPassword;
    private String token;
}
