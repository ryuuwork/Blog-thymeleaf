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
    @Email(message = "Email invalidate")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
