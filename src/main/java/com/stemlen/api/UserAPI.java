package com.stemlen.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.LoginDTO;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.dto.UserDTO;
import com.stemlen.entity.User;
import com.stemlen.exception.PortalException;
import com.stemlen.service.UserService;
import com.stemlen.service.EmailVerificationService;
import com.stemlen.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;


@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
public class UserAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Register User API
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws PortalException {
        userDTO = userService.registerUser(userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    // ✅ Login API
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody @Valid LoginDTO loginDTO) throws PortalException {
        return new ResponseEntity<>(userService.loginUser(loginDTO), HttpStatus.OK);
    }

    // ✅ Change Password API
    @PostMapping("/changepassword")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid LoginDTO loginDTO) throws PortalException {
        return new ResponseEntity<>(userService.changePassword(loginDTO), HttpStatus.OK);
    }

    // ✅ Send OTP API
    @PostMapping("/sendOtp/{email}")
    public ResponseEntity<ResponseDTO> sendOtp(@PathVariable @Email(message = "{user.email.invalid}") String email) {
        try {
            userService.sendOtp(email);
            return new ResponseEntity<>(new ResponseDTO("OTP Sent Successfully"), HttpStatus.OK);
        } catch (PortalException ex) {
            return new ResponseEntity<>(new ResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error in sending OTP: " + e.getMessage());
            return new ResponseEntity<>(new ResponseDTO("Error while sending OTP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Verify OTP API
    @GetMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<ResponseDTO> verifyOtp(
            @PathVariable @Email(message = "{user.email.invalid}") String email,
            @PathVariable @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") String otp) {
        try {
            boolean isValid = userService.verifyOtp(email, otp);
            if (isValid) {
                return new ResponseEntity<>(new ResponseDTO("OTP Verified Successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseDTO("Invalid OTP"), HttpStatus.BAD_REQUEST);
            }
        } catch (PortalException ex) {
            return new ResponseEntity<>(new ResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error in verifying OTP: " + e.getMessage());
            return new ResponseEntity<>(new ResponseDTO("Error while verifying OTP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Send Email Verification Link
    @PostMapping("/send-verification-email")
    public ResponseEntity<ResponseDTO> sendVerificationEmail(@RequestBody @Valid Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("Email parameter is missing."));
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("User not found."));
        }

        User user = userOptional.get();
        if (user.isEmailVerified()) {
            return ResponseEntity.ok(new ResponseDTO("Email is already verified."));
        }

        // Generate & Save Verification Token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        // Send Email
        boolean emailSent = emailVerificationService.sendVerificationEmail(user.getEmail());

        if (!emailSent) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("Failed to send email. Try again later."));
        }

        return ResponseEntity.ok(new ResponseDTO("Verification email sent successfully."));
    }
    
    @PostMapping("/resend-verification-email")
    public ResponseEntity<ResponseDTO> resendVerificationEmail(@RequestBody @Valid Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("Email parameter is missing."));
        }

        try {
            boolean emailSent = emailVerificationService.resendVerificationEmail(email);

            if (emailSent) {
                return ResponseEntity.ok(new ResponseDTO("Verification email resent successfully."));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("User not found or email already verified."));
            }
        } catch (Exception e) {
            System.err.println("Error resending verification email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(new ResponseDTO("An error occurred while resending the email."));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDTO> verifyEmail(@RequestParam String token) {
        try {
            boolean isVerified = emailVerificationService.verifyEmail(token);

            if (isVerified) {
                return ResponseEntity.ok(new ResponseDTO("Email Verified Successfully."));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid or Expired Verification Token."));
            }
        } catch (Exception e) {
            System.err.println("Error verifying email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(new ResponseDTO("An error occurred while verifying the email."));
        }
    }
}
