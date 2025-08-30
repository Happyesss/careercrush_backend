package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.exception.PortalException;

public interface TrialSessionService {
    
    // 🔒 SECURE CREATE: Create session with ownership validation
    TrialSessionDTO createAvailableSlot(TrialSessionDTO trialSessionDTO) throws PortalException;
    
    // Create multiple slots for a mentor
    List<TrialSessionDTO> createMultipleAvailableSlots(Long mentorId, List<LocalDateTime> dateTimeSlots, Integer durationMinutes) throws PortalException;
    
    // 🔒 SECURE READ: Get sessions with ownership validation
    TrialSessionDTO getTrialSession(Long id) throws PortalException;
    List<TrialSessionDTO> getTrialSessionsByMentor(Long mentorId);
    List<TrialSessionDTO> getAvailableSessionsByMentor(Long mentorId);
    
    // 🔒 SECURE UPDATE: Update session with ownership validation
    TrialSessionDTO updateTrialSessionWithOwnership(Long sessionId, TrialSessionDTO updatedSession, Long userId) throws PortalException;
    TrialSessionDTO updateTrialSessionStatus(Long id, TrialSessionStatus status) throws PortalException;
    
    // 🔒 SECURE DELETE: Delete session with ownership validation
    void deleteTrialSessionWithOwnership(Long sessionId, Long userId) throws PortalException;
    
    // Booking operations (public - can be done by mentees)
    TrialSessionDTO bookTrialSession(Long sessionId, String menteeEmail, String menteeName, String menteePhone) throws PortalException;
    TrialSessionDTO cancelTrialSession(Long id) throws PortalException;
    TrialSessionDTO completeTrialSession(Long id, String notes) throws PortalException;
    
    // Query operations
    List<TrialSessionDTO> getTrialSessionsByMentee(Long menteeId);
    List<TrialSessionDTO> getTrialSessionsByPackage(Long packageId);
    List<TrialSessionDTO> getAvailableSessionsForDate(LocalDateTime date);
    List<TrialSessionDTO> getBookedSessionsByEmail(String menteeEmail);
    
    // Admin/utility operations
    List<TrialSessionDTO> findOrphanedTrialSessions() throws PortalException;
    String cleanupOrphanedTrialSessions() throws PortalException;
    
    // 🔒 CORE SECURITY METHOD: Validate session ownership
    void validateTrialSessionOwnership(Long sessionId, Long userId) throws PortalException;
}
