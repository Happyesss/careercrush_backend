package com.stemlen.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.stemlen.entity.TrialSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrialSessionDTO {
    private Long id;
    private Long mentorId;
    private Long menteeId;
    private Long packageId;
    private LocalDateTime scheduledDateTime;
    private Integer durationMinutes;
    private TrialSessionStatus status;
    private String sessionType;
    private String meetingLink;
    private String notes;
    private String menteeEmail;
    private String menteeName;
    private String menteePhone;
    private List<String> availableTimeSlots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TrialSession toEntity() {
        TrialSession entity = new TrialSession();
        entity.setId(this.id);
        entity.setMentorId(this.mentorId);
        entity.setMenteeId(this.menteeId);
        entity.setPackageId(this.packageId);
        entity.setScheduledDateTime(this.scheduledDateTime);
        entity.setDurationMinutes(this.durationMinutes);
        entity.setStatus(this.status);
        entity.setSessionType(this.sessionType);
        entity.setMeetingLink(this.meetingLink);
        entity.setNotes(this.notes);
        entity.setMenteeEmail(this.menteeEmail);
        entity.setMenteeName(this.menteeName);
        entity.setMenteePhone(this.menteePhone);
        entity.setAvailableTimeSlots(this.availableTimeSlots);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
    }
}