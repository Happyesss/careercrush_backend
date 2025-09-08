package com.stemlen.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.stemlen.entity.MentorshipPackage;
import com.stemlen.entity.PackageModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipPackageDTO {
    private Long id;
    private Long mentorId;
    private String packageName;
    private String description;
    private Integer durationMonths;
    private Integer totalSessions;
    private Integer sessionsPerMonth;
    private Double pricePerMonth;
    private Double totalPrice;
    private List<String> topicsCovered;
    private List<PackageModule> modules;
    private Boolean isActive;
    private Boolean isFreeTrialIncluded;
    private String sessionType;
    private Integer sessionDurationMinutes;
    
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
    
    public MentorshipPackage toEntity() {
        MentorshipPackage entity = new MentorshipPackage();
        entity.setId(this.id);
        entity.setMentorId(this.mentorId);
        entity.setPackageName(this.packageName);
        entity.setDescription(this.description);
        entity.setDurationMonths(this.durationMonths);
        entity.setTotalSessions(this.totalSessions);
        entity.setSessionsPerMonth(this.sessionsPerMonth);
        entity.setPricePerMonth(this.pricePerMonth);
        entity.setTotalPrice(this.totalPrice);
        entity.setTopicsCovered(this.topicsCovered);
        entity.setModules(this.modules);
        entity.setIsActive(this.isActive);
        entity.setIsFreeTrialIncluded(this.isFreeTrialIncluded);
        entity.setSessionType(this.sessionType);
        entity.setSessionDurationMinutes(this.sessionDurationMinutes);
        entity.setHasUnlimitedChat(this.hasUnlimitedChat);
        entity.setHasCuratedTasks(this.hasCuratedTasks);
        entity.setHasRegularFollowups(this.hasRegularFollowups);
        entity.setHasJobReferrals(this.hasJobReferrals);
        entity.setHasCertification(this.hasCertification);
        entity.setHasRescheduling(this.hasRescheduling);
        entity.setThreeMonthDiscount(this.threeMonthDiscount);
        entity.setSixMonthDiscount(this.sixMonthDiscount);
        entity.setOriginalPricePerMonth(this.originalPricePerMonth);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
    }
}