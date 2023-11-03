package com.tuananhdo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDTO {
    @Email(message = "{forgot.password.valid.email}")
    @NotBlank(message = "{forgot.password.email.not.blank}")
    private String email;
}
