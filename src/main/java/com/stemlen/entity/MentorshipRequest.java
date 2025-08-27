package com.stemlen.entity;

import java.time.LocalDateTime;

import com.stemlen.dto.MentorshipRequestDTO;
import com.stemlen.dto.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequest {
    private Long requestId;
    private String menteeName;
    private String menteeEmail;
    private Long menteePhone;
    private String menteeBackground;
    private String requestMessage;
    private String goals;
    private String preferredTime;
    private String sessionType;
    private LocalDateTime requestTime;
    private SessionStatus sessionStatus;
    private LocalDateTime scheduledTime;
    private String mentorNotes;
    
    public MentorshipRequestDTO toDTO() {
        return new MentorshipRequestDTO(
            this.requestId, this.menteeName, this.menteeEmail, this.menteePhone,
            this.menteeBackground, this.requestMessage, this.goals, this.preferredTime,
            this.sessionType, this.requestTime, this.sessionStatus, this.scheduledTime, this.mentorNotes
        );
    }
}