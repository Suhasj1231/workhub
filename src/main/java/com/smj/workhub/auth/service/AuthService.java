package com.smj.workhub.auth.service;

import com.smj.workhub.auth.dto.*;

public interface AuthService {

    void register(RegisterRequest request);

    void verifyEmail(String token);

//    AuthResponse login(LoginRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getCurrentUser();



}