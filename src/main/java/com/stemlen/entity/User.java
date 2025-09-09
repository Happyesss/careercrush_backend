package com.stemlen.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.AccountType;
import com.stemlen.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private Long id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;
    private AccountType accountType;
    private Long profileId;

    private String provider; // e.g., "google", "github"

    private boolean emailVerified = false;

    private String verificationToken; // ✅ Added missing field

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    // Convert User entity to UserDTO
    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setEmail(this.email);
        dto.setPassword(this.password);
        dto.setAccountType(this.accountType);
        dto.setProfileId(this.profileId);
        dto.setProvider(this.provider);
        dto.setEmailVerified(this.emailVerified);
        dto.setVerificationToken(this.verificationToken);
        return dto;
    }
}
