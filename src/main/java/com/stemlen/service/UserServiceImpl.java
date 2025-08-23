package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stemlen.dto.AccountType;
import com.stemlen.dto.LoginDTO;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.dto.UserDTO;
import com.stemlen.entity.OTP;
import com.stemlen.entity.User;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.OTPRepository;
import com.stemlen.repository.UserRepository;
import com.stemlen.utility.OTPTemp;
import com.stemlen.utility.Utilities;

import jakarta.mail.internet.MimeMessage;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserDTO registerUser(UserDTO userDTO) throws PortalException {
        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if (optional.isPresent()) {
            throw new PortalException("USER_FOUND");
        }

        // ✅ Fix: Correct sequence key ("users") 
        userDTO.setId(Utilities.getNextSequence("users"));

        // Create a profile for the user
        userDTO.setProfileId(profileService.createProfile(userDTO.getEmail(), userDTO.getName()));
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User user = userDTO.toEntity();
        user = userRepository.save(user);

        return user.toDTO();
    }

    @Override
    public User registerOAuthUser(User user) throws PortalException {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        // ✅ Fix: Correct sequence key ("users") 
        user.setId(Utilities.getNextSequence("users"));

        // Set default values for OAuth2 users
        user.setPassword(passwordEncoder.encode("OAUTH_USER_NO_PASSWORD"));
        user.setAccountType(AccountType.APPLICANT);
        user.setProfileId(profileService.createProfile(user.getEmail(), user.getName()));

        return userRepository.save(user);
    }

    @Override
    public UserDTO loginUser(LoginDTO loginDTO) throws PortalException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new PortalException("USER_NOT_FOUND"));
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new PortalException("INVALID_CREDENTIALS");
        }

        return user.toDTO();
    }

    @Override
    public Boolean sendOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PortalException("USER_NOT_FOUND"));

        String genOTP = Utilities.generateOTP();

        OTP otp = new OTP(email, genOTP, LocalDateTime.now());
        otpRepository.save(otp);

        MimeMessage mm = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mm, true);
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText(OTPTemp.getMessageBody(genOTP, user.getName()), true);

        mailSender.send(mm);
        return true;
    }

    @Override
    public Boolean verifyOtp(String email, String otp) throws PortalException {
        OTP storedOtp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new PortalException("OTP_NOT_FOUND"));

        if (!storedOtp.getOtpCode().equals(otp)) {
            throw new PortalException("INVALID_OTP");
        }
        if (storedOtp.getCreationTime().plusMinutes(10).isBefore(LocalDateTime.now())) {
            throw new PortalException("OTP_EXPIRED");
        }

        return true;
    }

    @Override
    public ResponseDTO changePassword(LoginDTO loginDTO) throws PortalException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new PortalException("USER_NOT_FOUND"));
        
        user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        userRepository.save(user);
        
        return new ResponseDTO("Password changed successfully.");
    }
    
    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTPs() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(10);
        List<OTP> expiredOTPs = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOTPs.isEmpty()) {
            otpRepository.deleteAll(expiredOTPs);
        }
    }

    @Override
    public UserDTO getUserByEmail(String email) throws PortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new PortalException("USER_NOT_FOUND")).toDTO();
    }
}
