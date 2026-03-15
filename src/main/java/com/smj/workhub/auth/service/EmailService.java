package com.smj.workhub.auth.service;

public interface EmailService {

    void sendVerificationEmail(String to, String token);

}