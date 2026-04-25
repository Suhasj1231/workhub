package com.smj.workhub.auth.dto;

//import com.smj.workhub.user.enums.UserStatus;

import com.smj.workhub.user.entity.UserStatus;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        UserStatus status,
        String role
) {}