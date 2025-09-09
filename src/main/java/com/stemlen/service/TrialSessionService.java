package com.stemlen.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.dto.BulkTrialSessionDTO;
import com.stemlen.dto.AvailabilityTemplateDTO;
import com.stemlen.exception.PortalException;

public interface TrialSessionService {
    
    // 🔒 SECURE CREATE: Create session with ownership validation
    TrialSessionDTO createAvailableSlot(TrialSessionDTO trialSessionDTO) throws PortalException;
    
    // Create multiple slots for a mentor
    List<TrialSessionDTO> createMultipleAvailableSlots(Long mentorId, List<LocalDateTime> dateTimeSlots, Integer durationMinutes) throws PortalException;
    
    // 🆕 ENHANCED BULK OPERATIONS
    List<TrialSessionDTO> createBulkTrialSessions(BulkTrialSessionDTO bulkRequest, Long mentorId) throws PortalException;
    List<TrialSessionDTO> createRecurringTrialSessions(TrialSessionDTO baseSession, String recurringPattern, LocalDateTime endDate, Long mentorId) throws PortalException;
    
    // 🆕 AVAILABILITY TEMPLATE OPERATIONS
    AvailabilityTemplateDTO saveAvailabilityTemplate(AvailabilityTemplateDTO template, Long mentorId) throws PortalException;
    List<AvailabilityTemplateDTO> getAvailabilityTemplatesByMentor(Long mentorId);
    AvailabilityTemplateDTO getAvailabilityTemplate(Long templateId, Long mentorId) throws PortalException;
    void deleteAvailabilityTemplate(Long templateId, Long mentorId) throws PortalException;
    List<TrialSessionDTO> applyAvailabilityTemplate(Long templateId, LocalDateTime startDate, LocalDateTime endDate, Long mentorId) throws PortalException;
    
    // 🔒 SECURE READ: Get sessions with ownership validation
    TrialSessionDTO getTrialSession(Long id) throws PortalException;
    List<TrialSessionDTO> getTrialSessionsByMentor(Long mentorId);
    List<TrialSessionDTO> getAvailableSessionsByMentor(Long mentorId);
    
    // 🆕 ENHANCED QUERY OPERATIONS
    List<TrialSessionDTO> getTrialSessionsByDateRange(Long mentorId, LocalDateTime startDate, LocalDateTime endDate);
    List<TrialSessionDTO> getTrialSessionsByTimeSlot(Long mentorId, LocalTime startTime, LocalTime endTime, List<Integer> daysOfWeek);
    List<TrialSessionDTO> getConflictingSessions(Long mentorId, LocalDateTime scheduledDateTime, Integer durationMinutes, Integer bufferMinutes);
    
    // 🔒 SECURE UPDATE: Update session with ownership validation
    TrialSessionDTO updateTrialSessionWithOwnership(Long sessionId, TrialSessionDTO updatedSession, Long userId) throws PortalException;
    TrialSessionDTO updateTrialSessionStatus(Long id, TrialSessionStatus status) throws PortalException;
    
    // 🆕 BULK UPDATE OPERATIONS
    List<TrialSessionDTO> updateMultipleTrialSessions(List<Long> sessionIds, TrialSessionDTO updates, Long mentorId) throws PortalException;
    void deleteMultipleTrialSessions(List<Long> sessionIds, Long mentorId) throws PortalException;
    
    // 🔒 SECURE DELETE: Delete session with ownership validation
    void deleteTrialSessionWithOwnership(Long sessionId, Long userId) throws PortalException;
    
    // Booking operations (public - can be done by mentees)
    TrialSessionDTO bookTrialSession(Long sessionId, String menteeEmail, String menteeName, String menteePhone) throws PortalException;
    TrialSessionDTO cancelTrialSession(Long id) throws PortalException;
    TrialSessionDTO completeTrialSession(Long id, String notes) throws PortalException;
    
    // 🆕 ENHANCED BOOKING OPERATIONS
    TrialSessionDTO rescheduleTrialSession(Long sessionId, LocalDateTime newDateTime, String reason) throws PortalException;
    List<TrialSessionDTO> findAlternativeSlots(Long originalSessionId, LocalDateTime preferredDateTime, Integer durationHours) throws PortalException;
    
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
