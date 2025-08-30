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
        return new TrialSessionDTO(
            this.id,
            this.mentorId,
            this.menteeId,
            this.packageId,
            this.scheduledDateTime,
            this.durationMinutes,
            this.status,
            this.sessionType,
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
