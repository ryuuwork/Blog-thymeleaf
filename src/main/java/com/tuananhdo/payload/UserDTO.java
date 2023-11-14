package com.tuananhdo.payload;

import com.tuananhdo.entity.Role;
import com.tuananhdo.utils.AuthenticationType;
import com.tuananhdo.utils.PhotoPathUtil;
import com.tuananhdo.validate.AddUserValidate;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    @NotBlank(message = "{user.name.not.blank}")
    private String name;
    @NotBlank(message = "{user.email.not.blank}")
    @Email(message = "{user.email.valid}")
    private String email;
    @NotBlank(message = "{user.password.not.blank}", groups = AddUserValidate.class)
    @Size(min = 8, max = 20, message = "{user.password.size}", groups = {AddUserValidate.class})
    private String password;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;
    private boolean enabled;
    private String photos;
    @NotBlank(message = "{user.address.not.blank}")
    private String address;
    @NotBlank(message = "{user.phone.not.blank}")
    private String phoneNumber;
    private String resetPasswordToken;
    private String verificationCode;
    private LocalDateTime resetPasswordTokenExpirationTime;
    @NotEmpty(message = "{user.roles.valid}")
    Set<Role> roles = new HashSet<>();

    public String getUserPhotoPath() {
        return PhotoPathUtil.getPhotoPath(this.id, this.photos);
    }
}
