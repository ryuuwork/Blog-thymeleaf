package com.tuananhdo.payload;

import com.tuananhdo.entity.Role;
import com.tuananhdo.utils.AuthenticationType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
    @Email(message = "{user.email}")
    private String email;
    private String hidentMail;
    @NotBlank(message = "{user.password.not.blank}")
    @Size(min = 8,max = 20,message = "{user.password.size}")
    private String password;
    @NotBlank(message = "{user.password.new.not.blank}")
    @Size(min = 8,max = 20,message = "{user.password.new.size}")private String newPassword;
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
    Set<Role> roles = new HashSet<>();
    @Transient
    public String getUserPhotoPath(){
        if (id == null || photos == null || photos.isEmpty()){
            return "/common/assets/images/products/s1.jpg";
        }
        return "/user-photos/"+this.id + "/"+ this.photos;
    }
}
