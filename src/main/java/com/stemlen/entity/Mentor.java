package com.stemlen.entity;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.Certification;
import com.stemlen.dto.Experience;
import com.stemlen.dto.MentorDTO;
import com.stemlen.dto.MentorshipStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mentors")
public class Mentor {
    @Id
    private Long id;
    private String name;
    private String email;
    private String jobTitle;
    private String company;
    private String location;
    private String about;
    private byte[] picture;
    private byte[] profileBackground;
    private Long totalExp;
    private List<String> skills;
    private List<Experience> experiences;
    private List<Certification> certifications;
    
    // Mentor-specific fields
    private String expertise; // Areas of expertise
    private String bio; // Detailed bio for mentorship
    private List<String> mentorshipAreas; // Specific areas they provide mentorship in
    private Boolean isAvailable; // Current availability status
    private Integer currentMentees; // Current number of active mentees
    private MentorshipStatus mentorshipStatus; // ACTIVE, INACTIVE, BUSY
    private LocalDateTime joinDate; // When they joined as a mentor
    private String linkedinUrl; // LinkedIn profile
    private String portfolioUrl; // Portfolio/website URL
    private List<String> languages; // Languages spoken
    private String timezone; // Mentor's timezone
    private List<String> availableDays; // Days available for mentorship
    private String sessionPreference; // Video call, chat, email, etc.
    private List<MentorshipRequest> mentorshipRequests; // List of mentorship requests
    
    public MentorDTO toDTO() {
        return new MentorDTO(
            this.id, this.name, this.email, this.jobTitle, this.company, this.location, this.about,
            this.picture != null ? Base64.getEncoder().encodeToString(this.picture) : null,
            this.profileBackground != null ? Base64.getEncoder().encodeToString(this.profileBackground) : null,
            this.totalExp, this.skills, this.experiences, this.certifications,
            this.expertise, this.bio, this.mentorshipAreas, this.isAvailable,
            this.currentMentees, this.mentorshipStatus, this.joinDate,
            this.linkedinUrl, this.portfolioUrl, this.languages, this.timezone,
            this.availableDays, this.sessionPreference,
            this.mentorshipRequests != null ? this.mentorshipRequests.stream().map(MentorshipRequest::toDTO).toList() : null
        );
    }
}