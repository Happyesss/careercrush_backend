package com.stemlen.service;

public interface EmailVerificationService {
    boolean sendVerificationEmail(String email);
    boolean verifyEmail(String token);
    boolean resendVerificationEmail(String email);
}
