package com.tuananhdo.entity;


import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.utils.AuthenticationType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private boolean enabled;
    @Column(length = 64)
    private String photos;
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;
    @Column(name = "verification_code", length = 64, updatable = false)
    private String verificationCode;
    @Column(name = "reset_password_token", length = 30)
    private String resetPasswordToken;
    @Column(name = "reset_password_token_expiration_time")
    private LocalDateTime resetPasswordTokenExpirationTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
            , inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    public void coppyFields(UserDTO another){
        setName(another.getName());
        setEmail(another.getEmail());
        setPhotos(another.getPhotos());
        setEnabled(true);
        setRoles(another.getRoles());
    }
}
