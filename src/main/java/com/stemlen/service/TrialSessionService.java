package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.exception.PortalException;

public interface TrialSessionService {
    
    // Create available trial session slots for a mentor
    TrialSessionDTO createAvailableSlot(TrialSessionDTO trialSessionDTO) throws PortalException;
    
    // Book a trial session
    TrialSessionDTO bookTrialSession(Long sessionId, String menteeEmail, String menteeName, String menteePhone) throws PortalException;
    
    // Get available trial sessions for a mentor
    List<TrialSessionDTO> getAvailableSessionsByMentor(Long mentorId);
    
    // Get trial sessions by mentor
    List<TrialSessionDTO> getTrialSessionsByMentor(Long mentorId);
    
    // Get trial sessions by mentee
    List<TrialSessionDTO> getTrialSessionsByMentee(Long menteeId);
    
    // Get trial sessions by package
    List<TrialSessionDTO> getTrialSessionsByPackage(Long packageId);
    
    // Get trial session by ID
    TrialSessionDTO getTrialSession(Long id) throws PortalException;
    
    // Update trial session status
    TrialSessionDTO updateTrialSessionStatus(Long id, TrialSessionStatus status) throws PortalException;
    
    // Cancel trial session
    TrialSessionDTO cancelTrialSession(Long id) throws PortalException;
    
    // Complete trial session
    TrialSessionDTO completeTrialSession(Long id, String notes) throws PortalException;
    
    // Get available sessions for a specific date
    List<TrialSessionDTO> getAvailableSessionsForDate(LocalDateTime date);
    
    // Get booked sessions by mentee email
    List<TrialSessionDTO> getBookedSessionsByEmail(String menteeEmail);
    
    // Create multiple available slots for a mentor
    List<TrialSessionDTO> createMultipleAvailableSlots(Long mentorId, List<LocalDateTime> dateTimeSlots, Integer durationMinutes) throws PortalException;
    
    // Delete trial session
    void deleteTrialSession(Long id) throws PortalException;
}