package com.stemlen.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.MentorshipPackageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mentorship_packages")
public class MentorshipPackage {
    @Id
    private Long id;
    private Long mentorId; // Reference to the mentor who created this package
    private String packageName; // e.g., "6 Months Complete Mentorship"
    private String description; // Package description
    private Integer durationMonths; // Duration in months (1, 3, 6, 12)
    private Integer totalSessions; // Total number of sessions in the package
    private Integer sessionsPerMonth; // Sessions per month
    private Double pricePerMonth; // Monthly price
    private Double totalPrice; // Total package price
    private List<String> topicsCovered; // Topics that will be covered
    private List<PackageModule> modules; // Monthly breakdown modules
    private Boolean isActive; // Whether the package is currently active
    private Boolean isFreeTrialIncluded; // Whether free trial is included
    private String sessionType; // Video call, chat, email, etc.
    private Integer sessionDurationMinutes; // Duration of each session
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public MentorshipPackageDTO toDTO() {
        return new MentorshipPackageDTO(
            this.id, this.mentorId, this.packageName, this.description,
            this.durationMonths, this.totalSessions, this.sessionsPerMonth,
            this.pricePerMonth, this.totalPrice, this.topicsCovered, this.modules,
            this.isActive, this.isFreeTrialIncluded, this.sessionType,
            this.sessionDurationMinutes, this.createdAt, this.updatedAt
        );
    }
}