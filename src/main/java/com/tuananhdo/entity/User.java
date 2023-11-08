package com.tuananhdo.entity;


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
    @Column(length = 70)
    private String address;
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;
    @Column(name = "verification_code", length = 128, updatable = false)
    private String verificationCode;
    @Column(name = "reset_password_token", length = 128)
    private String resetPasswordToken;
    @Column(name = "reset_password_token_expiration_time")
    private LocalDateTime resetPasswordTokenExpirationTime;
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;
    @Column(name = "failed_attempt", nullable = false)
    private int failedAttempt;
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
            , inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

}
