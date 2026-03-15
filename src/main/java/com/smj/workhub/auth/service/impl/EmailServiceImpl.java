package com.smj.workhub.auth.service.impl;

import com.smj.workhub.auth.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${server.port}")
    private String serverPort;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String token) {

        String verificationLink =
                "http://localhost:" + serverPort +
                        "/api/v1/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Verify your WorkHub account");
        message.setText(
                "Welcome to WorkHub!\n\n" +
                        "Please verify your email by clicking the link below:\n\n" +
                        verificationLink + "\n\n" +
                        "If you did not register, please ignore this email."
        );

        mailSender.send(message);
    }
}