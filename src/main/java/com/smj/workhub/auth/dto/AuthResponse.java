package com.smj.workhub.auth.dto;

public record AuthResponse(

        String accessToken,
        String tokenType

) {}