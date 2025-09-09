package com.stemlen.entity;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "trial_sessions")
public class TrialSession {
    
    @Id
    private Long id;
    
    // 🔒 OWNERSHIP FIELD - Critical for security
    private Long mentorId;          // The mentor who created this session
    
    private Long menteeId;          // Who booked the session (nullable until booked)
    private Long packageId;         // Associated mentorship package (optional)
    
    private LocalDateTime scheduledDateTime;
    private Integer durationMinutes;
    private TrialSessionStatus status;
    private String sessionType;
    
    // Enhanced scheduling fields
    private String timeZone;               // Time zone for the session
    private Integer bufferTimeMinutes;     // Buffer time between sessions
    private Integer preparationTimeMinutes; // Preparation time before session
    private String recurringPattern;       // DAILY, WEEKLY, MONTHLY, CUSTOM
    private LocalDateTime recurringEndDate; // When recurring pattern ends
    private Boolean isRecurring;           // Is this part of a recurring series
    private Long parentSessionId;          // Reference to original session if recurring
    private String availabilityTemplate;  // Template name for reusing patterns
    
    // Session configuration
    private String sessionTitle;          // Custom title for the session
    private String sessionDescription;    // Brief description
    private Boolean allowRescheduling;    // Allow mentees to reschedule
    private Integer maxReschedulingHours; // Max hours before session to reschedule
    private Boolean requireConfirmation;  // Require manual confirmation
    private String specialInstructions;   // Special instructions for mentees
    
    // Meeting details
    private String meetingLink;
    private String meetingId;
    private String meetingPassword;
    
    // Mentee details (when booked)
    private String menteeEmail;
    private String menteeName;
    private String menteePhone;
    
    // Session notes and tracking
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    
    /**
     * Convert entity to DTO
     */
    public TrialSessionDTO toDTO() {
        TrialSessionDTO dto = new TrialSessionDTO();
        dto.setId(this.id);
        dto.setMentorId(this.mentorId);
        dto.setMenteeId(this.menteeId);
        dto.setPackageId(this.packageId);
        dto.setScheduledDateTime(this.scheduledDateTime);
        dto.setDurationMinutes(this.durationMinutes);
        dto.setStatus(this.status);
        dto.setSessionType(this.sessionType);
        
        // Enhanced scheduling fields
        dto.setTimeZone(this.timeZone);
        dto.setBufferTimeMinutes(this.bufferTimeMinutes);
        dto.setPreparationTimeMinutes(this.preparationTimeMinutes);
        dto.setRecurringPattern(this.recurringPattern);
        dto.setRecurringEndDate(this.recurringEndDate);
        dto.setIsRecurring(this.isRecurring);
        dto.setParentSessionId(this.parentSessionId);
        dto.setAvailabilityTemplate(this.availabilityTemplate);
        
        // Session configuration
        dto.setSessionTitle(this.sessionTitle);
        dto.setSessionDescription(this.sessionDescription);
        dto.setAllowRescheduling(this.allowRescheduling);
        dto.setMaxReschedulingHours(this.maxReschedulingHours);
        dto.setRequireConfirmation(this.requireConfirmation);
        dto.setSpecialInstructions(this.specialInstructions);
        
        // Meeting details
        dto.setMeetingLink(this.meetingLink);
        dto.setMeetingId(this.meetingId);
        dto.setMeetingPassword(this.meetingPassword);
        
        // Mentee details
        dto.setMenteeEmail(this.menteeEmail);
        dto.setMenteeName(this.menteeName);
        dto.setMenteePhone(this.menteePhone);
        
        // Tracking
        dto.setNotes(this.notes);
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);
        dto.setCompletedAt(this.completedAt);
        
        return dto;
    }
}
