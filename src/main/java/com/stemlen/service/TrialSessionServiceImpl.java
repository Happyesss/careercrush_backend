package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    
    @Override
    public TrialSessionDTO createAvailableSlot(TrialSessionDTO trialSessionDTO) throws PortalException {
        // Validate that the mentor exists
        if (trialSessionDTO.getMentorId() != null) {
            mentorRepository.findById(trialSessionDTO.getMentorId())
                    .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: Mentor with ID " + trialSessionDTO.getMentorId() + " does not exist"));
        } else {
            throw new PortalException("MENTOR_ID_REQUIRED: Mentor ID is required for creating trial session");
        }
        
        if (Objects.isNull(trialSessionDTO.getId()) || trialSessionDTO.getId() == 0) {
            trialSessionDTO.setId(Utilities.getNextSequence("trialSessions"));
            trialSessionDTO.setCreatedAt(LocalDateTime.now());
        }
        trialSessionDTO.setUpdatedAt(LocalDateTime.now());
        trialSessionDTO.setStatus(TrialSessionStatus.AVAILABLE);
        
        // Set default duration if not provided
        if (trialSessionDTO.getDurationMinutes() == null) {
            trialSessionDTO.setDurationMinutes(30); // Default 30-minute trial
        }
        
        TrialSession savedSession = trialSessionRepository.save(trialSessionDTO.toEntity());
        return savedSession.toDTO();
    }
    
    @Override
    public TrialSessionDTO bookTrialSession(Long sessionId, String menteeEmail, String menteeName, String menteePhone) throws PortalException {
        TrialSession session = trialSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        if (session.getStatus() != TrialSessionStatus.AVAILABLE) {
            throw new PortalException("TRIAL_SESSION_NOT_AVAILABLE");
        }
        
        session.setStatus(TrialSessionStatus.BOOKED);
        session.setMenteeEmail(menteeEmail);
        session.setMenteeName(menteeName);
        session.setMenteePhone(menteePhone);
        session.setUpdatedAt(LocalDateTime.now());
        
        TrialSession bookedSession = trialSessionRepository.save(session);
        return bookedSession.toDTO();
    }
    
    @Override
    public List<TrialSessionDTO> getAvailableSessionsByMentor(Long mentorId) {
        return trialSessionRepository.findByMentorIdAndStatus(mentorId, TrialSessionStatus.AVAILABLE).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public List<TrialSessionDTO> getTrialSessionsByMentor(Long mentorId) {
        return trialSessionRepository.findByMentorId(mentorId).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public List<TrialSessionDTO> getTrialSessionsByMentee(Long menteeId) {
        return trialSessionRepository.findByMenteeId(menteeId).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public List<TrialSessionDTO> getTrialSessionsByPackage(Long packageId) {
        return trialSessionRepository.findByPackageId(packageId).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public TrialSessionDTO getTrialSession(Long id) throws PortalException {
        return trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"))
                .toDTO();
    }
    
    @Override
    public TrialSessionDTO updateTrialSessionStatus(Long id, TrialSessionStatus status) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(status);
        session.setUpdatedAt(LocalDateTime.now());
        
        TrialSession updatedSession = trialSessionRepository.save(session);
        return updatedSession.toDTO();
    }
    
    @Override
    public TrialSessionDTO cancelTrialSession(Long id) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(TrialSessionStatus.CANCELLED);
        session.setUpdatedAt(LocalDateTime.now());
        
        TrialSession cancelledSession = trialSessionRepository.save(session);
        return cancelledSession.toDTO();
    }
    
    @Override
    public TrialSessionDTO completeTrialSession(Long id, String notes) throws PortalException {
        TrialSession session = trialSessionRepository.findById(id)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        session.setStatus(TrialSessionStatus.COMPLETED);
        session.setNotes(notes);
        session.setUpdatedAt(LocalDateTime.now());
        
        TrialSession completedSession = trialSessionRepository.save(session);
        return completedSession.toDTO();
    }
    
    @Override
    public List<TrialSessionDTO> getAvailableSessionsForDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        
        return trialSessionRepository.findAvailableSessionsForDate(startOfDay, endOfDay).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public List<TrialSessionDTO> getBookedSessionsByEmail(String menteeEmail) {
        return trialSessionRepository.findByMenteeEmailAndStatus(menteeEmail, TrialSessionStatus.BOOKED).stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
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
                .toList();
        
        List<TrialSession> savedSessions = trialSessionRepository.saveAll(sessions);
        return savedSessions.stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public void deleteTrialSession(Long id) throws PortalException {
        if (!trialSessionRepository.existsById(id)) {
            throw new PortalException("TRIAL_SESSION_NOT_FOUND");
        }
        trialSessionRepository.deleteById(id);
    }
    
    @Override
    public List<TrialSessionDTO> findOrphanedTrialSessions() throws PortalException {
        List<TrialSession> allSessions = trialSessionRepository.findAll();
        List<TrialSession> orphanedSessions = allSessions.stream()
                .filter(session -> {
                    // Check if the mentorId exists in the mentor collection
                    if (session.getMentorId() == null) {
                        return true; // Sessions without mentorId are orphaned
                    }
                    return !mentorRepository.existsById(session.getMentorId());
                })
                .toList();
                
        return orphanedSessions.stream()
                .map(TrialSession::toDTO)
                .toList();
    }
    
    @Override
    public void cleanupOrphanedTrialSessions() throws PortalException {
        List<TrialSessionDTO> orphanedSessions = findOrphanedTrialSessions();
        for (TrialSessionDTO session : orphanedSessions) {
            deleteTrialSession(session.getId());
        }
    }
}