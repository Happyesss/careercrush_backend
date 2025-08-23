package com.stemlen.service;

import com.stemlen.entity.User;
import com.stemlen.repository.UserRepository;
import com.stemlen.utility.EmailVerificationTemp;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean sendVerificationEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false; 
        }

        User user = userOptional.get();
        if (user.isEmailVerified()) {
            return false;
        }

        // 🔹 Generate a new verification token and store it in the User entity
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);

        // 🔗 Create the verification link
        String verificationLink = "http://localhost:3000/verify-email?token=" + token;
        String emailBody = EmailVerificationTemp.getMessageBody(verificationLink, user.getName());

        try {
            sendEmail(email, "Verify Your Email", emailBody);
            return true;
        } catch (MessagingException e) {
            return false; 
        }
    }

    @Override
    public boolean resendVerificationEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false; 
        }

        User user = userOptional.get();
        if (user.isEmailVerified()) {
            return false; 
        }

       
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        userRepository.save(user);

      
        String verificationLink = "http://localhost:3000/verify-email?token=" + newToken;
        String emailBody = EmailVerificationTemp.getMessageBody(verificationLink, user.getName());

        try {
            sendEmail(email, "Verify Your Email", emailBody);
            return true;
        } catch (MessagingException e) {
            return false; 
        }
    }

    @Override
    public boolean verifyEmail(String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isEmailVerified()) {
                return false; 
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null); 
            userRepository.save(user);

            return true; 
        }

        return false; // Invalid token
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
