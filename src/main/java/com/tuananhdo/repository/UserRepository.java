package com.tuananhdo.repository;

import com.tuananhdo.entity.User;
import com.tuananhdo.paging.SearchRepository;
import com.tuananhdo.utils.AuthenticationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends SearchRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    @Transactional
    @Query("UPDATE User c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Long id, AuthenticationType type);

    @Query("SELECT u FROM User u WHERE  u.resetPasswordToken = ?1")
    User findByResetPasswordToken(String token);

    @Transactional
    @Query("UPDATE User u SET u.enabled= true,u.accountNonLocked = true,u.verificationCode = null WHERE u.id = ?1")
    @Modifying
    void enableUserAndRemoveToken(Long id);

    @Transactional
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Long id, boolean enabled);

    @Query("SELECT c FROM User c WHERE c.verificationCode = ?1")
    User findByVerificationCode(String token);

    @Query("SELECT u FROM User u WHERE  u.id = ?1")
    User getUserById(Long id);

    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findAllById(List<Long> userIds);

    @Query("SELECT u FROM User u WHERE CONCAT(LOWER(u.id), '',LOWER(u.email), '',LOWER(u.name)) LIKE %?1%")
    Page<User> findAllByKeyWord(Pageable pageable, String keyword);

    @Transactional
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempt(int failedAttempt, String email);

    @Query("SELECT u FROM User u WHERE u.accountNonLocked = false AND u.lockTime < ?1")
    List<User> findAllAccountExpired(LocalDateTime nowDateTime);

    @Query("SELECT u FROM User u WHERE u.resetPasswordToken IS NOT NULL AND u.resetPasswordTokenExpirationTime < ?1")
    List<User> findAllTokenResetPasswordExpired(LocalDateTime timeExpired);

    @Query("SELECT u FROM User u where u.email LIKE %?1")
    List<User> findByEmailEndingWith(String suffix);
}
