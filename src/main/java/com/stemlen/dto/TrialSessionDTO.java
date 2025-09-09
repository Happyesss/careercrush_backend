package com.stemlen.dto;

import java.time.LocalDateTime;

import com.stemlen.entity.TrialSession;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrialSessionDTO {
    
    private Long id;
    
    // @NotNull(message = "Mentor ID is required") - removed for API endpoint, set from JWT
    private Long mentorId;          // The mentor who owns this session
    
    private Long menteeId;          // Who booked the session
    private Long packageId;         // Associated mentorship package
    
    @NotNull(message = "Scheduled date/time is required")
    private LocalDateTime scheduledDateTime;
    
    private Integer durationMinutes = 30; // Default 30 minutes
    private TrialSessionStatus status = TrialSessionStatus.AVAILABLE;
    private String sessionType = "Video Call";
    
    // Enhanced scheduling fields
    private String timeZone = "UTC";        // Time zone for the session
    private Integer bufferTimeMinutes = 5;  // Buffer time between sessions
    private Integer preparationTimeMinutes = 10; // Preparation time before session
    private String recurringPattern;        // DAILY, WEEKLY, MONTHLY, CUSTOM
    private LocalDateTime recurringEndDate; // When recurring pattern ends
    private Boolean isRecurring = false;    // Is this part of a recurring series
    private Long parentSessionId;           // Reference to original session if recurring
    private String availabilityTemplate;   // Template name for reusing patterns
    
    // Session configuration
    private String sessionTitle;           // Custom title for the session
    private String sessionDescription;     // Brief description
    private Boolean allowRescheduling = true; // Allow mentees to reschedule
    private Integer maxReschedulingHours = 24; // Max hours before session to reschedule
    private Boolean requireConfirmation = false; // Require manual confirmation
    private String specialInstructions;    // Special instructions for mentees
    
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
     * Convert DTO to entity
     */
    public TrialSession toEntity() {
        TrialSession entity = new TrialSession();
        entity.setId(this.id);
        entity.setMentorId(this.mentorId);
        entity.setMenteeId(this.menteeId);
        entity.setPackageId(this.packageId);
        entity.setScheduledDateTime(this.scheduledDateTime);
        entity.setDurationMinutes(this.durationMinutes != null ? this.durationMinutes : 30);
        entity.setStatus(this.status != null ? this.status : TrialSessionStatus.AVAILABLE);
        entity.setSessionType(this.sessionType != null ? this.sessionType : "Video Call");
        
        // Enhanced scheduling fields
        entity.setTimeZone(this.timeZone != null ? this.timeZone : "UTC");
        entity.setBufferTimeMinutes(this.bufferTimeMinutes != null ? this.bufferTimeMinutes : 5);
        entity.setPreparationTimeMinutes(this.preparationTimeMinutes != null ? this.preparationTimeMinutes : 10);
        entity.setRecurringPattern(this.recurringPattern);
        entity.setRecurringEndDate(this.recurringEndDate);
        entity.setIsRecurring(this.isRecurring != null ? this.isRecurring : false);
        entity.setParentSessionId(this.parentSessionId);
        entity.setAvailabilityTemplate(this.availabilityTemplate);
        
        // Session configuration
        entity.setSessionTitle(this.sessionTitle);
        entity.setSessionDescription(this.sessionDescription);
        entity.setAllowRescheduling(this.allowRescheduling != null ? this.allowRescheduling : true);
        entity.setMaxReschedulingHours(this.maxReschedulingHours != null ? this.maxReschedulingHours : 24);
        entity.setRequireConfirmation(this.requireConfirmation != null ? this.requireConfirmation : false);
        entity.setSpecialInstructions(this.specialInstructions);
        
        // Meeting details
        entity.setMeetingLink(this.meetingLink);
        entity.setMeetingId(this.meetingId);
        entity.setMeetingPassword(this.meetingPassword);
        
        // Mentee details
        entity.setMenteeEmail(this.menteeEmail);
        entity.setMenteeName(this.menteeName);
        entity.setMenteePhone(this.menteePhone);
        
        // Tracking
        entity.setNotes(this.notes);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        entity.setCompletedAt(this.completedAt);
        
        return entity;
    }
}
