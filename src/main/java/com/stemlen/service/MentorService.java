package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;

import com.stemlen.dto.MentorDTO;
import com.stemlen.dto.MentorshipRequestDTO;
import com.stemlen.dto.MentorshipStatus;
import com.stemlen.dto.SessionStatus;
import com.stemlen.exception.PortalException;

public interface MentorService {
    
    // Create or update mentor profile
    public MentorDTO createMentor(MentorDTO mentorDTO) throws PortalException;
    
    // Update mentor profile
    public MentorDTO updateMentor(MentorDTO mentorDTO) throws PortalException;
    
    // Get mentor by ID
    public MentorDTO getMentor(Long id) throws PortalException;
    
    // Get mentor by email
    public MentorDTO getMentorByEmail(String email) throws PortalException;
    
    // Get all mentors
    public List<MentorDTO> getAllMentors();
    
    // Get available mentors
    public List<MentorDTO> getAvailableMentors();
    
    // Get mentors by mentorship status
    public List<MentorDTO> getMentorsByStatus(MentorshipStatus status);
    
    // Get mentors by expertise area
    public List<MentorDTO> getMentorsByExpertise(String expertise);
    
    // Get mentors by skills
    public List<MentorDTO> getMentorsBySkill(String skill);
    
    // Get mentors by location
    public List<MentorDTO> getMentorsByLocation(String location);
    
    // Update mentor availability
    public MentorDTO updateMentorAvailability(Long id, Boolean isAvailable) throws PortalException;
    
    // Update mentor status
    public MentorDTO updateMentorStatus(Long id, MentorshipStatus status) throws PortalException;
    
    // Assign mentee to mentor (increment currentMentees)
    public MentorDTO assignMentee(Long mentorId) throws PortalException;
    
    // Remove mentee from mentor (decrement currentMentees)
    public MentorDTO removeMentee(Long mentorId) throws PortalException;
    
    // Delete mentor profile
    public void deleteMentor(Long id) throws PortalException;
    
    // Request mentorship session
    public void requestMentorshipSession(Long mentorId, MentorshipRequestDTO requestDTO) throws PortalException;
    
    // Update mentorship request status
    public void updateMentorshipRequestStatus(Long mentorId, Long requestId, SessionStatus status, LocalDateTime scheduledTime) throws PortalException;
}