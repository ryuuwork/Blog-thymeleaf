package com.tuananhdo.payload;

import com.tuananhdo.utils.PhotoPathUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class AccountDTO {
    private Long id;
    @NotBlank(message = "{account.name.not.blank}")
    private String name;
    @NotBlank(message = "{account.email.not.blank}")
    @Email(message = "{account.email}")
    private String email;
    @NotBlank(message = "{account.password.not.blank}")
    @Size(min = 8, max = 20, message = "{account.password.size}")
    private String password;
    @NotBlank(message = "{account.password.new.not.blank}")
    @Size(min = 8, max = 20, message = "{account.password.new.size}")
    private String newPassword;
    private String photos;
    private boolean enabled;
    @NotBlank(message = "{account.address.not.blank}")
    private String address;
    @NotBlank(message = "{account.phone.not.blank}")
    private String phoneNumber;
    public String getAccountPhotoPath() {
        return PhotoPathUtil.getPhotoPath(this.id, this.photos);
    }
}
