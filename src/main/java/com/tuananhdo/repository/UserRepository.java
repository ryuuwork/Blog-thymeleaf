package com.tuananhdo.repository;

import com.tuananhdo.entity.User;
import com.tuananhdo.utils.AuthenticationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

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

    @Query("SELECT u FROM User u WHERE CONCAT(LOWER(u.id), '',LOWER(u.email), '',LOWER(u.name)) LIKE %?1%")
    Page<User> findAllUserByKeyWord(Pageable pageable, String keyword);
}
