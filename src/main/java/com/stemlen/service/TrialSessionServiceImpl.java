package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.entity.TrialSession;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorRepository;
import com.stemlen.repository.TrialSessionRepository;
import com.stemlen.utility.Utilities;

@Service("trialSessionService")
public class TrialSessionServiceImpl implements TrialSessionService {
    
    @Autowired
    private TrialSessionRepository trialSessionRepository;
    
    @Autowired
    private MentorRepository mentorRepository;
    
    // 🔒 CORE SECURITY METHOD: Validate trial session ownership
    @Override
    public void validateTrialSessionOwnership(Long sessionId, Long userId) throws PortalException {
        TrialSession session = trialSessionRepository.findById(sessionId)
            .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND: Session with ID " + sessionId + " does not exist"));
        
        if (!session.getMentorId().equals(userId)) {
            throw new PortalException("UNAUTHORIZED_ACCESS: You can only access your own trial sessions");
        }
    }
    
    // 🔒 SECURE CREATE: Create available trial session slot
    @Override
    public TrialSessionDTO createAvailableSlot(TrialSessionDTO trialSessionDTO) throws PortalException {
        // Validate that the mentor exists
        if (trialSessionDTO.getMentorId() != null) {
            mentorRepository.findById(trialSessionDTO.getMentorId())
                    .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: Mentor with ID " + trialSessionDTO.getMentorId() + " does not exist"));
        } else {
            throw new PortalException("MENTOR_ID_REQUIRED: Mentor ID is required for creating trial session");
        }
        
        // Set ID and timestamps for new sessions
        if (Objects.isNull(trialSessionDTO.getId()) || trialSessionDTO.getId() == 0) {
            trialSessionDTO.setId(Utilities.getNextSequence("trialSessions"));
            trialSessionDTO.setCreatedAt(LocalDateTime.now());
        }
        trialSessionDTO.setUpdatedAt(LocalDateTime.now());
        trialSessionDTO.setStatus(TrialSessionStatus.AVAILABLE);
        
        // Set default values
        if (trialSessionDTO.getDurationMinutes() == null) {
            trialSessionDTO.setDurationMinutes(30);
        }
        if (trialSessionDTO.getSessionType() == null) {
            trialSessionDTO.setSessionType("Video Call");
        }
        
        return trialSessionRepository.save(trialSessionDTO.toEntity()).toDTO();
    }
    
    // 🔒 SECURE READ: Get trial session by ID
    @Override
    public TrialSessionDTO getTrialSession(Long id) throws PortalException {
        return trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND: Session with ID " + id + " does not exist"))
                .toDTO();
    }
    
    // 🔒 SECURE UPDATE: Update trial session with ownership validation
    @Override
    public TrialSessionDTO updateTrialSessionWithOwnership(Long sessionId, TrialSessionDTO updatedSession, Long userId) throws PortalException {
        // Validate ownership first
        validateTrialSessionOwnership(sessionId, userId);
        
        TrialSession existingSession = trialSessionRepository.findById(sessionId)
            .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        // Update allowed fields while preserving ownership and critical data
        existingSession.setScheduledDateTime(updatedSession.getScheduledDateTime());
        existingSession.setDurationMinutes(updatedSession.getDurationMinutes());
        existingSession.setSessionType(updatedSession.getSessionType());
        existingSession.setMeetingLink(updatedSession.getMeetingLink());
        existingSession.setMeetingId(updatedSession.getMeetingId());
        existingSession.setMeetingPassword(updatedSession.getMeetingPassword());
        existingSession.setNotes(updatedSession.getNotes());
        existingSession.setUpdatedAt(LocalDateTime.now());
        
        // 🔒 CRITICAL: mentorId cannot be changed - preserve ownership
        // existingSession.setMentorId() is NOT called to prevent ownership transfer
        
        return trialSessionRepository.save(existingSession).toDTO();
    }
    
    // 🔒 SECURE DELETE: Delete trial session with ownership validation
    @Override
    public void deleteTrialSessionWithOwnership(Long sessionId, Long userId) throws PortalException {
        // Validate ownership before deletion
        validateTrialSessionOwnership(sessionId, userId);
        
        // Additional validation: Don't delete booked sessions
        TrialSession session = trialSessionRepository.findById(sessionId)
            .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        if (session.getStatus() == TrialSessionStatus.BOOKED) {
            throw new PortalException("CANNOT_DELETE_BOOKED_SESSION: Cannot delete a session that has been booked");
        }
        
        trialSessionRepository.deleteById(sessionId);
    }
    
    // Get sessions by mentor (ownership-based filtering)
    @Override
    public List<TrialSessionDTO> getTrialSessionsByMentor(Long mentorId) {
        return trialSessionRepository.findByMentorId(mentorId).stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TrialSessionDTO> getAvailableSessionsByMentor(Long mentorId) {
        return trialSessionRepository.findByMentorIdAndStatus(mentorId, TrialSessionStatus.AVAILABLE).stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    // Create multiple available slots for a mentor
    @Override
    public List<TrialSessionDTO> createMultipleAvailableSlots(Long mentorId, List<LocalDateTime> dateTimeSlots, Integer durationMinutes) throws PortalException {
        // Validate that the mentor exists before creating any sessions
        mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: Mentor with ID " + mentorId + " does not exist"));
        
        List<TrialSession> sessions = dateTimeSlots.stream()
                .map(dateTime -> {
                    TrialSession session = new TrialSession();
                    try {
                        session.setId(Utilities.getNextSequence("trialSessions"));
                    } catch (PortalException e) {
                        throw new RuntimeException(e);
                    }
                    session.setMentorId(mentorId);
                    session.setScheduledDateTime(dateTime);
                    session.setDurationMinutes(durationMinutes != null ? durationMinutes : 30);
                    session.setStatus(TrialSessionStatus.AVAILABLE);
                    session.setSessionType("Video Call");
                    session.setCreatedAt(LocalDateTime.now());
                    session.setUpdatedAt(LocalDateTime.now());
                    return session;
                })
                .collect(Collectors.toList());
        
        List<TrialSession> savedSessions = trialSessionRepository.saveAll(sessions);
        return savedSessions.stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    // PUBLIC BOOKING OPERATIONS (can be done by mentees)
    @Override
    public TrialSessionDTO bookTrialSession(Long sessionId, String menteeEmail, String menteeName, String menteePhone) throws PortalException {
        TrialSession session = trialSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        if (session.getStatus() != TrialSessionStatus.AVAILABLE) {
            throw new PortalException("SESSION_NOT_AVAILABLE: Session is not available for booking");
        }
        
        // Update session with booking details
        session.setStatus(TrialSessionStatus.BOOKED);
        session.setMenteeEmail(menteeEmail);
        session.setMenteeName(menteeName);
        session.setMenteePhone(menteePhone);
        session.setUpdatedAt(LocalDateTime.now());
        
        return trialSessionRepository.save(session).toDTO();
    }
    
    @Override
    public TrialSessionDTO cancelTrialSession(Long id) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(TrialSessionStatus.CANCELLED);
        session.setUpdatedAt(LocalDateTime.now());
        
        return trialSessionRepository.save(session).toDTO();
    }
    
    @Override
    public TrialSessionDTO completeTrialSession(Long id, String notes) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(TrialSessionStatus.COMPLETED);
        if (notes != null) {
            session.setNotes(notes);
        }
        session.setCompletedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        
        return trialSessionRepository.save(session).toDTO();
    }
    
    @Override
    public TrialSessionDTO updateTrialSessionStatus(Long id, TrialSessionStatus status) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(status);
        session.setUpdatedAt(LocalDateTime.now());
        
        return trialSessionRepository.save(session).toDTO();
    }
    
    // QUERY OPERATIONS
    @Override
    public List<TrialSessionDTO> getTrialSessionsByMentee(Long menteeId) {
        return trialSessionRepository.findByMenteeId(menteeId).stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TrialSessionDTO> getTrialSessionsByPackage(Long packageId) {
        return trialSessionRepository.findByPackageId(packageId).stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TrialSessionDTO> getAvailableSessionsForDate(LocalDateTime date) {
        // Get available sessions for the entire day
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        return trialSessionRepository.findAll().stream()
                .filter(session -> session.getStatus() == TrialSessionStatus.AVAILABLE)
                .filter(session -> session.getScheduledDateTime().isAfter(startOfDay) && 
                                 session.getScheduledDateTime().isBefore(endOfDay))
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TrialSessionDTO> getBookedSessionsByEmail(String menteeEmail) {
        return trialSessionRepository.findByMenteeEmailAndStatus(menteeEmail, TrialSessionStatus.BOOKED).stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    // ADMIN/UTILITY OPERATIONS
    @Override
    public List<TrialSessionDTO> findOrphanedTrialSessions() throws PortalException {
        List<TrialSession> allSessions = trialSessionRepository.findAll();
        return allSessions.stream()
                .filter(session -> {
                    try {
                        return !mentorRepository.existsById(session.getMentorId());
                    } catch (Exception e) {
                        return true; // Consider it orphaned if we can't verify
                    }
                })
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public String cleanupOrphanedTrialSessions() throws PortalException {
        List<TrialSessionDTO> orphanedSessions = findOrphanedTrialSessions();
        
        if (orphanedSessions.isEmpty()) {
            return "No orphaned trial sessions found";
        }
        
        List<Long> orphanedIds = orphanedSessions.stream()
                .map(TrialSessionDTO::getId)
                .collect(Collectors.toList());
        
        trialSessionRepository.deleteAllById(orphanedIds);
        
        return "Cleaned up " + orphanedSessions.size() + " orphaned trial sessions";
    }
}
