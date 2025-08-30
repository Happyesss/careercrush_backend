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
    
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;          // The mentor who owns this session
    
    private Long menteeId;          // Who booked the session
    private Long packageId;         // Associated mentorship package
    
    @NotNull(message = "Scheduled date/time is required")
    private LocalDateTime scheduledDateTime;
    
    private Integer durationMinutes = 30; // Default 30 minutes
    private TrialSessionStatus status = TrialSessionStatus.AVAILABLE;
    private String sessionType = "Video Call";
    
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
        return new TrialSession(
            this.id,
            this.mentorId,
            this.menteeId,
            this.packageId,
            this.scheduledDateTime,
            this.durationMinutes != null ? this.durationMinutes : 30,
            this.status != null ? this.status : TrialSessionStatus.AVAILABLE,
            this.sessionType != null ? this.sessionType : "Video Call",
            this.meetingLink,
            this.meetingId,
            this.meetingPassword,
            this.menteeEmail,
            this.menteeName,
            this.menteePhone,
            this.notes,
            this.createdAt,
            this.updatedAt,
            this.completedAt
        );
    }
}
