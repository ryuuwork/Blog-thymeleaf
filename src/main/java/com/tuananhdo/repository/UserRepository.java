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

    @Query("UPDATE User c SET c.authenticationType = :type WHERE c.id = :id")
    @Modifying
    void updateAuthenticationType(Long id, AuthenticationType type);

    @Query("SELECT u FROM User u WHERE  u.resetPasswordToken = :token")
    User findByResetPasswordToken(String token);

    @Transactional
    @Query("UPDATE User u SET u.enabled = true WHERE u.id = :id")
    @Modifying
    void enableUser(Long id);

    @Transactional
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Long id, boolean enabled);

    @Query("SELECT c FROM User c WHERE c.verificationCode = :token")
    User findByVerificationCode(String token);

    @Query("SELECT u FROM User u WHERE  u.id = ?1")
    User getUserById(Long id);

    List<User> findAll();

    @Query("SELECT u FROM User u WHERE CONCAT(LOWER(u.id), '',LOWER(u.email), '',LOWER(u.name)) LIKE %?1%")
    Page<User> findAllByKeyWord(Pageable pageable, String keyword);

    @Transactional
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempt(int failedAttempt, String email);

    @Query("SELECT u FROM User u WHERE u.accountNonLocked = false AND u.lockTime < ?1")
    List<User> findAllAccountExpired(LocalDateTime nowDateTime);
}
