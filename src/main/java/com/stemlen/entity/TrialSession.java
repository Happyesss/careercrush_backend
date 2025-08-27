package com.stemlen.entity;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long mentorId; // Reference to mentor
    private Long menteeId; // Reference to mentee (can be null for available slots)
    private Long packageId; // Reference to the package being trialed
    private LocalDateTime scheduledDateTime; // When the trial is scheduled
    private Integer durationMinutes; // Duration of trial session (usually 30 mins)
    private TrialSessionStatus status; // AVAILABLE, BOOKED, COMPLETED, CANCELLED
    private String sessionType; // Video call, phone, etc.
    private String meetingLink; // Video call link
    private String notes; // Any notes from mentor/mentee
    private String menteeEmail; // Email of person booking trial
    private String menteeName; // Name of person booking trial
    private String menteePhone; // Phone number (optional)
    private List<String> availableTimeSlots; // For available sessions
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TrialSessionDTO toDTO() {
        return new TrialSessionDTO(
            this.id, this.mentorId, this.menteeId, this.packageId,
            this.scheduledDateTime, this.durationMinutes, this.status,
            this.sessionType, this.meetingLink, this.notes,
            this.menteeEmail, this.menteeName, this.menteePhone,
            this.availableTimeSlots, this.createdAt, this.updatedAt
        );
    }
}