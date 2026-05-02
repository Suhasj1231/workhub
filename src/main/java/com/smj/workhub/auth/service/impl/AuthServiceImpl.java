package com.smj.workhub.auth.service.impl;

import com.smj.workhub.auth.dto.*;
import com.smj.workhub.auth.service.AuthService;
import com.smj.workhub.auth.service.EmailService;
import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.security.jwt.JwtService;
import com.smj.workhub.security.principal.UserPrincipal;
import com.smj.workhub.user.entity.AuthProvider;
import com.smj.workhub.user.entity.User;
import com.smj.workhub.user.entity.UserStatus;
import com.smj.workhub.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    // ------------------------------------------------
    // REGISTER
    // ------------------------------------------------

    @Override
    public void register(RegisterRequest request) {

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + email
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        String verificationToken = UUID.randomUUID().toString();
        String verificationTokenHash = hashToken(verificationToken);

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPasswordHash(passwordHash);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setVerificationTokenHash(verificationTokenHash);

        // email immutable → set via reflection workaround avoided
        // better approach: constructor or builder (future improvement)

        try {
            var emailField = User.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(user, email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set email", e);
        }

        userRepository.save(user);

        emailService.sendVerificationEmail(email, verificationToken);

        // emailService.sendVerificationEmail(email, verificationToken);
    }

    // ------------------------------------------------
    // VERIFY EMAIL
    // ------------------------------------------------
    @Transactional
    @Override
    public void verifyEmail(String token) {

        String tokenHash = hashToken(token);

        User user = userRepository
                .findByVerificationTokenHash(tokenHash)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid verification token")
                );

        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationTokenHash(null);
    }

    // ------------------------------------------------
    // LOGIN
    // ------------------------------------------------

/*
    @Override
    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid credentials")
                );

        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Email not verified");
        }

        boolean passwordMatches =
                passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new IllegalStateException("Invalid credentials");
        }


        String token = "JWT_TOKEN_PLACEHOLDER";

        return new AuthResponse(token, "Bearer");
    }

 */

    @Override
    public LoginResponse login(LoginRequest request) {

//        String email = request.email().toLowerCase().trim();

        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid email or password")
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("Email not verified");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(token);
    }



    // ------------------------------------------------
    // HELPERS
    // ------------------------------------------------

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found")
                );

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.getRole().name()
        );
    }

}