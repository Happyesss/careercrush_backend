package com.stemlen.dto;

import java.time.LocalDateTime;

import com.stemlen.entity.MentorshipRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDTO {
    private Long requestId;
    private String menteeName;
    private String menteeEmail;
    private Long menteePhone;
    private String menteeBackground; // Background/current position
    private String requestMessage; // Message to mentor
    private String goals; // What mentee wants to achieve
    private String preferredTime; // Preferred session time
    private String sessionType; // One-time, ongoing, project-based
    private LocalDateTime requestTime;
    private SessionStatus sessionStatus;
    private LocalDateTime scheduledTime;
    private String mentorNotes; // Notes from mentor
    
    public MentorshipRequest toEntity() {
        return new MentorshipRequest(
            this.requestId, this.menteeName, this.menteeEmail, this.menteePhone,
            this.menteeBackground, this.requestMessage, this.goals, this.preferredTime,
            this.sessionType, this.requestTime, this.sessionStatus, this.scheduledTime, this.mentorNotes
        );
    }
}