package com.smj.workhub.auth.controller;

import com.smj.workhub.auth.dto.LoginRequest;
import com.smj.workhub.auth.dto.LoginResponse;
import com.smj.workhub.auth.dto.RegisterRequest;
import com.smj.workhub.auth.dto.UserResponse;
import com.smj.workhub.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------------- REGISTER ----------------

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and sends an email verification link"
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
    }

    // ---------------- LOGIN ----------------

    @Operation(
            summary = "User login",
            description = "Authenticates the user and returns a JWT token"
    )
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    // ---------------- VERIFY EMAIL ----------------

    @Operation(
            summary = "Verify email",
            description = "Verifies the user's email using the verification token"
    )
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {

        authService.verifyEmail(token);

        return "Email verified successfully. You can now login.";
    }

    @GetMapping("/me")
    public UserResponse me() {
        return authService.getCurrentUser();
    }
}
