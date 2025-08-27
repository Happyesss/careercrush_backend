package com.stemlen.dto;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import com.stemlen.entity.Mentor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {
    private Long id;
    private String name;
    private String email;
    private String jobTitle;
    private String company;
    private String location;
    private String about;
    private String picture; // Base64 encoded image
    private String profileBackground; // Base64 encoded image
    private Long totalExp;
    private List<String> skills;
    private List<Experience> experiences;
    private List<Certification> certifications;
    
    // Mentor-specific fields
    private String expertise; // Areas of expertise
    private Double hourlyRate; // Hourly rate for mentorship
    private String bio; // Detailed bio for mentorship
    private List<String> mentorshipAreas; // Specific areas they provide mentorship in
    private Boolean isAvailable; // Current availability status
    private Integer maxMentees; // Maximum number of mentees they can handle
    private Integer currentMentees; // Current number of active mentees
    private MentorshipStatus mentorshipStatus; // ACTIVE, INACTIVE, BUSY
    private LocalDateTime joinDate; // When they joined as a mentor
    private String linkedinUrl; // LinkedIn profile
    private String portfolioUrl; // Portfolio/website URL
    private List<String> languages; // Languages spoken
    private String timezone; // Mentor's timezone
    private List<String> availableDays; // Days available for mentorship
    private String sessionPreference; // Video call, chat, email, etc.
    private List<MentorshipRequestDTO> mentorshipRequests; // List of mentorship requests
    
    public Mentor toEntity() {
        return new Mentor(
            this.id, this.name, this.email, this.jobTitle, this.company, this.location, this.about,
            this.picture != null ? Base64.getDecoder().decode(this.picture) : null,
            this.profileBackground != null ? Base64.getDecoder().decode(this.profileBackground) : null,
            this.totalExp, this.skills, this.experiences, this.certifications,
            this.expertise, this.hourlyRate, this.bio, this.mentorshipAreas, this.isAvailable,
            this.maxMentees, this.currentMentees, this.mentorshipStatus, this.joinDate,
            this.linkedinUrl, this.portfolioUrl, this.languages, this.timezone,
            this.availableDays, this.sessionPreference,
            this.mentorshipRequests != null ? this.mentorshipRequests.stream().map(MentorshipRequestDTO::toEntity).toList() : null
        );
    }
}