package com.smj.workhub.user.repository;

import com.smj.workhub.user.entity.User;
import com.smj.workhub.user.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // -------- AUTHENTICATION --------

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);// -------- EMAIL VERIFICATION --------

    Optional<User> findByVerificationTokenHash(String verificationTokenHash);

    // -------- OAUTH SUPPORT (future) --------

    Optional<User> findByProviderUserIdAndAuthProvider(
            String providerUserId,
            AuthProvider authProvider
    );
}