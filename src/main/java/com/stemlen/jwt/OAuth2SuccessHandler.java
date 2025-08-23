package com.stemlen.jwt;

import com.stemlen.dto.AccountType;
import com.stemlen.entity.User;

import com.stemlen.repository.UserRepository;
import com.stemlen.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;


@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final UserService userService;

    public OAuth2SuccessHandler(JwtHelper jwtHelper, 
                              UserRepository userRepository,
                              UserService userService) {
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null) {
                // Create new user from OAuth2 data
                user = new User();
                user.setEmail(email);
                user.setName(oAuth2User.getAttribute("name"));
                user.setAccountType(AccountType.APPLICANT); // Use APPLICANT for OAuth2 users
                user = userService.registerOAuthUser(user);
            }
            
            // Generate JWT token
            String token = jwtHelper.generateToken(new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getProfileId(),
                user.getAccountType(),
                new ArrayList<>(),
                user.isEmailVerified()
            ));
            
            // Redirect to the frontend with the token
            String frontendRedirectUrl = "https://www.stemlen.com/oauth2/redirect?token=" + token;
            response.sendRedirect(frontendRedirectUrl);
        } catch (Exception e) {
            // Log the error and redirect to an error page
            logger.error("OAuth2 login failed", e);
            response.sendRedirect("https://www.stemlen.com/error?message=OAuth2 login failed");
        }
    }
}