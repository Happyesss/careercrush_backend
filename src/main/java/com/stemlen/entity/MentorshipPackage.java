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
    
    // Package Inclusions (as per Preplaced documentation)
    private Boolean hasUnlimitedChat; // Unlimited chat with mentor
    private Boolean hasCuratedTasks; // Task & Curated Resources
    private Boolean hasRegularFollowups; // Regular follow-ups (accountability)
    private Boolean hasJobReferrals; // Job referrals from mentor community
    private Boolean hasCertification; // Certification of mentorship completion
    private Boolean hasRescheduling; // Reschedule anytime capability
    
    // Discount fields for pricing strategy
    private Double threeMonthDiscount; // Discount percentage for 3-month plan
    private Double sixMonthDiscount; // Discount percentage for 6-month plan
    private Double originalPricePerMonth; // Original price before discounts
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public MentorshipPackageDTO toDTO() {
        MentorshipPackageDTO dto = new MentorshipPackageDTO();
        dto.setId(this.id);
        dto.setMentorId(this.mentorId);
        dto.setPackageName(this.packageName);
        dto.setDescription(this.description);
        dto.setDurationMonths(this.durationMonths);
        dto.setTotalSessions(this.totalSessions);
        dto.setSessionsPerMonth(this.sessionsPerMonth);
        dto.setPricePerMonth(this.pricePerMonth);
        dto.setTotalPrice(this.totalPrice);
        dto.setTopicsCovered(this.topicsCovered);
        dto.setModules(this.modules);
        dto.setIsActive(this.isActive);
        dto.setIsFreeTrialIncluded(this.isFreeTrialIncluded);
        dto.setSessionType(this.sessionType);
        dto.setSessionDurationMinutes(this.sessionDurationMinutes);
        dto.setHasUnlimitedChat(this.hasUnlimitedChat);
        dto.setHasCuratedTasks(this.hasCuratedTasks);
        dto.setHasRegularFollowups(this.hasRegularFollowups);
        dto.setHasJobReferrals(this.hasJobReferrals);
        dto.setHasCertification(this.hasCertification);
        dto.setHasRescheduling(this.hasRescheduling);
        dto.setThreeMonthDiscount(this.threeMonthDiscount);
        dto.setSixMonthDiscount(this.sixMonthDiscount);
        dto.setOriginalPricePerMonth(this.originalPricePerMonth);
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);
        return dto;
    }
}