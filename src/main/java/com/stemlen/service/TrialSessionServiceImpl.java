package com.stemlen.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.dto.BulkTrialSessionDTO;
import com.stemlen.dto.AvailabilityTemplateDTO;
import com.stemlen.entity.TrialSession;
import com.stemlen.entity.AvailabilityTemplate;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorRepository;
import com.stemlen.repository.TrialSessionRepository;
import com.stemlen.repository.AvailabilityTemplateRepository;
import com.stemlen.utility.Utilities;

@Service("trialSessionService")
public class TrialSessionServiceImpl implements TrialSessionService {
    
    @Autowired
    private TrialSessionRepository trialSessionRepository;
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Autowired
    private AvailabilityTemplateRepository availabilityTemplateRepository;
    
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
        
        // Set default values for new fields
        if (trialSessionDTO.getDurationMinutes() == null) {
            trialSessionDTO.setDurationMinutes(30);
        }
        if (trialSessionDTO.getSessionType() == null) {
            trialSessionDTO.setSessionType("Video Call");
        }
        if (trialSessionDTO.getTimeZone() == null) {
            trialSessionDTO.setTimeZone("UTC");
        }
        if (trialSessionDTO.getBufferTimeMinutes() == null) {
            trialSessionDTO.setBufferTimeMinutes(5);
        }
        if (trialSessionDTO.getPreparationTimeMinutes() == null) {
            trialSessionDTO.setPreparationTimeMinutes(10);
        }
        if (trialSessionDTO.getAllowRescheduling() == null) {
            trialSessionDTO.setAllowRescheduling(true);
        }
        if (trialSessionDTO.getMaxReschedulingHours() == null) {
            trialSessionDTO.setMaxReschedulingHours(24);
        }
        if (trialSessionDTO.getRequireConfirmation() == null) {
            trialSessionDTO.setRequireConfirmation(false);
        }
        if (trialSessionDTO.getIsRecurring() == null) {
            trialSessionDTO.setIsRecurring(false);
        }
        
        return trialSessionRepository.save(trialSessionDTO.toEntity()).toDTO();
    }

    // 🆕 ENHANCED BULK OPERATIONS
    @Override
    public List<TrialSessionDTO> createBulkTrialSessions(BulkTrialSessionDTO bulkRequest, Long mentorId) throws PortalException {
        // Validate mentor exists
        mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: Mentor with ID " + mentorId + " does not exist"));
        
        List<TrialSessionDTO> createdSessions = new ArrayList<>();
        LocalDateTime currentDate = bulkRequest.getStartDate().toLocalDate().atStartOfDay();
        LocalDateTime endDate = bulkRequest.getEndDate();
        
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            
            // Check if this day is included in the request
            if (bulkRequest.getDaysOfWeek() == null || bulkRequest.getDaysOfWeek().contains(dayOfWeek)) {
                // Create sessions for each time slot on this day
                for (BulkTrialSessionDTO.TimeSlotDTO timeSlot : bulkRequest.getTimeSlots()) {
                    LocalDateTime sessionDateTime = currentDate.toLocalDate().atTime(timeSlot.getStartTime());
                    
                    // Check for conflicts
                    Integer bufferMinutes = bulkRequest.getBufferTimeMinutes() != null ? 
                        bulkRequest.getBufferTimeMinutes() : 5;
                    List<TrialSessionDTO> conflicts = getConflictingSessions(mentorId, sessionDateTime, 
                        timeSlot.getDurationMinutes(), bufferMinutes);
                    
                    if (conflicts.isEmpty()) {
                        TrialSessionDTO sessionDTO = new TrialSessionDTO();
                        sessionDTO.setMentorId(mentorId);
                        sessionDTO.setPackageId(bulkRequest.getPackageId());
                        sessionDTO.setScheduledDateTime(sessionDateTime);
                        sessionDTO.setDurationMinutes(timeSlot.getDurationMinutes());
                        sessionDTO.setSessionType(bulkRequest.getSessionType());
                        sessionDTO.setTimeZone(bulkRequest.getTimeZone());
                        sessionDTO.setBufferTimeMinutes(bulkRequest.getBufferTimeMinutes());
                        sessionDTO.setPreparationTimeMinutes(bulkRequest.getPreparationTimeMinutes());
                        sessionDTO.setAllowRescheduling(bulkRequest.getAllowRescheduling());
                        sessionDTO.setMaxReschedulingHours(bulkRequest.getMaxReschedulingHours());
                        sessionDTO.setRequireConfirmation(bulkRequest.getRequireConfirmation());
                        sessionDTO.setSpecialInstructions(bulkRequest.getSpecialInstructions());
                        sessionDTO.setAvailabilityTemplate(bulkRequest.getAvailabilityTemplate());
                        sessionDTO.setSessionTitle(timeSlot.getSessionTitle());
                        sessionDTO.setSessionDescription(timeSlot.getSessionDescription());
                        
                        // Set recurring fields if applicable
                        if (bulkRequest.getCreateRecurring() != null && bulkRequest.getCreateRecurring()) {
                            sessionDTO.setIsRecurring(true);
                            sessionDTO.setRecurringPattern(bulkRequest.getRecurringPattern());
                            sessionDTO.setRecurringEndDate(endDate.plusWeeks(bulkRequest.getRecurringWeeks() - 1));
                        }
                        
                        TrialSessionDTO created = createAvailableSlot(sessionDTO);
                        createdSessions.add(created);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return createdSessions;
    }

    @Override
    public List<TrialSessionDTO> createRecurringTrialSessions(TrialSessionDTO baseSession, String recurringPattern, 
            LocalDateTime endDate, Long mentorId) throws PortalException {
        List<TrialSessionDTO> createdSessions = new ArrayList<>();
        LocalDateTime currentDateTime = baseSession.getScheduledDateTime();
        
        // Create the base session first
        baseSession.setIsRecurring(true);
        baseSession.setRecurringPattern(recurringPattern);
        baseSession.setRecurringEndDate(endDate);
        TrialSessionDTO parentSession = createAvailableSlot(baseSession);
        createdSessions.add(parentSession);
        
        // Create recurring sessions based on pattern
        while (currentDateTime.isBefore(endDate)) {
            switch (recurringPattern.toUpperCase()) {
                case "DAILY":
                    currentDateTime = currentDateTime.plusDays(1);
                    break;
                case "WEEKLY":
                    currentDateTime = currentDateTime.plusWeeks(1);
                    break;
                case "MONTHLY":
                    currentDateTime = currentDateTime.plusMonths(1);
                    break;
                default:
                    throw new PortalException("INVALID_RECURRING_PATTERN: Supported patterns are DAILY, WEEKLY, MONTHLY");
            }
            
            if (currentDateTime.isBefore(endDate) || currentDateTime.isEqual(endDate)) {
                TrialSessionDTO recurringSession = new TrialSessionDTO();
                // Copy all fields from base session
                recurringSession.setMentorId(baseSession.getMentorId());
                recurringSession.setPackageId(baseSession.getPackageId());
                recurringSession.setScheduledDateTime(currentDateTime);
                recurringSession.setDurationMinutes(baseSession.getDurationMinutes());
                recurringSession.setSessionType(baseSession.getSessionType());
                recurringSession.setTimeZone(baseSession.getTimeZone());
                recurringSession.setBufferTimeMinutes(baseSession.getBufferTimeMinutes());
                recurringSession.setPreparationTimeMinutes(baseSession.getPreparationTimeMinutes());
                recurringSession.setAllowRescheduling(baseSession.getAllowRescheduling());
                recurringSession.setMaxReschedulingHours(baseSession.getMaxReschedulingHours());
                recurringSession.setRequireConfirmation(baseSession.getRequireConfirmation());
                recurringSession.setSpecialInstructions(baseSession.getSpecialInstructions());
                recurringSession.setSessionTitle(baseSession.getSessionTitle());
                recurringSession.setSessionDescription(baseSession.getSessionDescription());
                
                // Set recurring fields
                recurringSession.setIsRecurring(true);
                recurringSession.setRecurringPattern(recurringPattern);
                recurringSession.setRecurringEndDate(endDate);
                recurringSession.setParentSessionId(parentSession.getId());
                
                // Check for conflicts before creating
                List<TrialSessionDTO> conflicts = getConflictingSessions(mentorId, currentDateTime, 
                    baseSession.getDurationMinutes(), baseSession.getBufferTimeMinutes());
                
                if (conflicts.isEmpty()) {
                    TrialSessionDTO created = createAvailableSlot(recurringSession);
                    createdSessions.add(created);
                }
            }
        }
        
        return createdSessions;
    }

    // 🆕 ENHANCED QUERY OPERATIONS
    @Override
    public List<TrialSessionDTO> getTrialSessionsByDateRange(Long mentorId, LocalDateTime startDate, LocalDateTime endDate) {
        return trialSessionRepository.findByMentorIdAndScheduledDateTimeBetween(mentorId, startDate, endDate)
                .stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrialSessionDTO> getTrialSessionsByTimeSlot(Long mentorId, LocalTime startTime, LocalTime endTime, 
            List<Integer> daysOfWeek) {
        return trialSessionRepository.findByMentorIdAndTimePattern(mentorId, 
                startTime.getHour(), endTime.getHour(), daysOfWeek)
                .stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrialSessionDTO> getConflictingSessions(Long mentorId, LocalDateTime scheduledDateTime, 
            Integer durationMinutes, Integer bufferMinutes) {
        LocalDateTime startTime = scheduledDateTime.minusMinutes(bufferMinutes);
        LocalDateTime endTime = scheduledDateTime.plusMinutes(durationMinutes + bufferMinutes);
        
        return trialSessionRepository.findConflictingSessions(mentorId, startTime, endTime)
                .stream()
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }

    // 🆕 AVAILABILITY TEMPLATE OPERATIONS
    @Override
    public AvailabilityTemplateDTO saveAvailabilityTemplate(AvailabilityTemplateDTO template, Long mentorId) throws PortalException {
        // Validate mentor exists
        mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: Mentor with ID " + mentorId + " does not exist"));
        
        // Ensure ownership
        template.setMentorId(mentorId);
        
        // Set ID if creating new template
        if (template.getId() == null) {
            template.setId(Utilities.getNextSequence("availabilityTemplates"));
        }
        
        // If this is being set as default, unset any existing default
        if (template.getIsDefault() != null && template.getIsDefault()) {
            AvailabilityTemplate existingDefault = availabilityTemplateRepository.findByMentorIdAndIsDefaultTrue(mentorId);
            if (existingDefault != null) {
                existingDefault.setIsDefault(false);
                availabilityTemplateRepository.save(existingDefault);
            }
        }
        
        AvailabilityTemplate entity = convertToEntity(template);
        return availabilityTemplateRepository.save(entity).toDTO();
    }

    @Override
    public List<AvailabilityTemplateDTO> getAvailabilityTemplatesByMentor(Long mentorId) {
        return availabilityTemplateRepository.findByMentorIdAndIsActiveTrue(mentorId)
                .stream()
                .map(AvailabilityTemplate::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AvailabilityTemplateDTO getAvailabilityTemplate(Long templateId, Long mentorId) throws PortalException {
        AvailabilityTemplate template = availabilityTemplateRepository.findByIdAndMentorId(templateId, mentorId);
        if (template == null) {
            throw new PortalException("AVAILABILITY_TEMPLATE_NOT_FOUND: Template not found or access denied");
        }
        return template.toDTO();
    }

    @Override
    public void deleteAvailabilityTemplate(Long templateId, Long mentorId) throws PortalException {
        AvailabilityTemplate template = availabilityTemplateRepository.findByIdAndMentorId(templateId, mentorId);
        if (template == null) {
            throw new PortalException("AVAILABILITY_TEMPLATE_NOT_FOUND: Template not found or access denied");
        }
        availabilityTemplateRepository.delete(template);
    }

    @Override
    public List<TrialSessionDTO> applyAvailabilityTemplate(Long templateId, LocalDateTime startDate, 
            LocalDateTime endDate, Long mentorId) throws PortalException {
        AvailabilityTemplateDTO template = getAvailabilityTemplate(templateId, mentorId);
        List<TrialSessionDTO> createdSessions = new ArrayList<>();
        
        LocalDateTime currentDate = startDate.toLocalDate().atStartOfDay();
        
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            
            // Find availability for this day of week
            AvailabilityTemplateDTO.DailyAvailabilityDTO dailyAvailability = template.getDailyAvailabilities()
                    .stream()
                    .filter(da -> da.getDayOfWeek().equals(dayOfWeek) && da.getIsAvailable())
                    .findFirst()
                    .orElse(null);
            
            if (dailyAvailability != null && dailyAvailability.getTimeSlots() != null) {
                for (AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO slot : dailyAvailability.getTimeSlots()) {
                    LocalDateTime sessionDateTime = currentDate.toLocalDate().atTime(slot.getStartTime());
                    
                    // Check for conflicts
                    List<TrialSessionDTO> conflicts = getConflictingSessions(mentorId, sessionDateTime, 
                        slot.getSessionDurationMinutes(), template.getBufferTimeMinutes());
                    
                    if (conflicts.isEmpty()) {
                        TrialSessionDTO sessionDTO = new TrialSessionDTO();
                        sessionDTO.setMentorId(mentorId);
                        sessionDTO.setScheduledDateTime(sessionDateTime);
                        sessionDTO.setDurationMinutes(slot.getSessionDurationMinutes());
                        sessionDTO.setSessionType(template.getDefaultSessionType());
                        sessionDTO.setBufferTimeMinutes(template.getBufferTimeMinutes());
                        sessionDTO.setPreparationTimeMinutes(template.getPreparationTimeMinutes());
                        sessionDTO.setAllowRescheduling(template.getAllowRescheduling());
                        sessionDTO.setMaxReschedulingHours(template.getMaxReschedulingHours());
                        sessionDTO.setRequireConfirmation(template.getRequireConfirmation());
                        sessionDTO.setAvailabilityTemplate(template.getTemplateName());
                        sessionDTO.setSessionTitle(slot.getSessionTitle());
                        sessionDTO.setSessionDescription(slot.getSessionDescription());
                        
                        TrialSessionDTO created = createAvailableSlot(sessionDTO);
                        createdSessions.add(created);
                    }
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return createdSessions;
    }

    // Helper method to convert DTO to entity
    private AvailabilityTemplate convertToEntity(AvailabilityTemplateDTO dto) {
        AvailabilityTemplate entity = new AvailabilityTemplate();
        entity.setId(dto.getId());
        entity.setMentorId(dto.getMentorId());
        entity.setTemplateName(dto.getTemplateName());
        entity.setDescription(dto.getDescription());
        entity.setDefaultDurationMinutes(dto.getDefaultDurationMinutes());
        entity.setDefaultSessionType(dto.getDefaultSessionType());
        entity.setBufferTimeMinutes(dto.getBufferTimeMinutes());
        entity.setPreparationTimeMinutes(dto.getPreparationTimeMinutes());
        entity.setAllowRescheduling(dto.getAllowRescheduling());
        entity.setMaxReschedulingHours(dto.getMaxReschedulingHours());
        entity.setRequireConfirmation(dto.getRequireConfirmation());
        entity.setIsDefault(dto.getIsDefault());
        entity.setIsActive(dto.getIsActive());
        
        // Convert daily availabilities
        if (dto.getDailyAvailabilities() != null) {
            List<AvailabilityTemplate.DailyAvailability> dailyAvailabilities = dto.getDailyAvailabilities()
                .stream()
                .map(this::convertDailyAvailabilityToEntity)
                .collect(Collectors.toList());
            entity.setDailyAvailabilities(dailyAvailabilities);
        }
        
        return entity;
    }
    
    private AvailabilityTemplate.DailyAvailability convertDailyAvailabilityToEntity(AvailabilityTemplateDTO.DailyAvailabilityDTO dto) {
        AvailabilityTemplate.DailyAvailability entity = new AvailabilityTemplate.DailyAvailability();
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setIsAvailable(dto.getIsAvailable());
        
        if (dto.getTimeSlots() != null) {
            List<AvailabilityTemplate.DailyAvailability.TimeSlotTemplate> timeSlots = dto.getTimeSlots()
                .stream()
                .map(this::convertTimeSlotToEntity)
                .collect(Collectors.toList());
            entity.setTimeSlots(timeSlots);
        }
        
        return entity;
    }
    
    private AvailabilityTemplate.DailyAvailability.TimeSlotTemplate convertTimeSlotToEntity(
            AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO dto) {
        AvailabilityTemplate.DailyAvailability.TimeSlotTemplate entity = 
            new AvailabilityTemplate.DailyAvailability.TimeSlotTemplate();
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setSessionDurationMinutes(dto.getSessionDurationMinutes());
        entity.setSessionTitle(dto.getSessionTitle());
        entity.setSessionDescription(dto.getSessionDescription());
        return entity;
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

    // 🆕 BULK UPDATE OPERATIONS
    @Override
    public List<TrialSessionDTO> updateMultipleTrialSessions(List<Long> sessionIds, TrialSessionDTO updates, Long mentorId) throws PortalException {
        // Validate ownership for all sessions
        List<TrialSession> sessions = trialSessionRepository.findByIdInAndMentorId(sessionIds, mentorId);
        if (sessions.size() != sessionIds.size()) {
            throw new PortalException("UNAUTHORIZED_ACCESS: Some sessions not found or access denied");
        }
        
        List<TrialSessionDTO> updatedSessions = new ArrayList<>();
        
        for (TrialSession session : sessions) {
            // Update only non-null fields from updates
            if (updates.getScheduledDateTime() != null) {
                session.setScheduledDateTime(updates.getScheduledDateTime());
            }
            if (updates.getDurationMinutes() != null) {
                session.setDurationMinutes(updates.getDurationMinutes());
            }
            if (updates.getSessionType() != null) {
                session.setSessionType(updates.getSessionType());
            }
            if (updates.getAllowRescheduling() != null) {
                session.setAllowRescheduling(updates.getAllowRescheduling());
            }
            if (updates.getRequireConfirmation() != null) {
                session.setRequireConfirmation(updates.getRequireConfirmation());
            }
            if (updates.getSpecialInstructions() != null) {
                session.setSpecialInstructions(updates.getSpecialInstructions());
            }
            
            session.setUpdatedAt(LocalDateTime.now());
            
            TrialSession saved = trialSessionRepository.save(session);
            updatedSessions.add(saved.toDTO());
        }
        
        return updatedSessions;
    }

    @Override
    public void deleteMultipleTrialSessions(List<Long> sessionIds, Long mentorId) throws PortalException {
        // Validate ownership for all sessions
        List<TrialSession> sessions = trialSessionRepository.findByIdInAndMentorId(sessionIds, mentorId);
        if (sessions.size() != sessionIds.size()) {
            throw new PortalException("UNAUTHORIZED_ACCESS: Some sessions not found or access denied");
        }
        
        // Additional validation: Don't delete booked sessions
        boolean hasBookedSessions = sessions.stream()
                .anyMatch(session -> session.getStatus() == TrialSessionStatus.BOOKED);
        
        if (hasBookedSessions) {
            throw new PortalException("CANNOT_DELETE_BOOKED_SESSION: Cannot delete sessions that are already booked");
        }
        
        trialSessionRepository.deleteAll(sessions);
    }

    // 🆕 ENHANCED BOOKING OPERATIONS
    @Override
    public TrialSessionDTO rescheduleTrialSession(Long sessionId, LocalDateTime newDateTime, String reason) throws PortalException {
        TrialSession session = trialSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        if (session.getStatus() != TrialSessionStatus.BOOKED) {
            throw new PortalException("INVALID_SESSION_STATUS: Only booked sessions can be rescheduled");
        }
        
        if (!session.getAllowRescheduling()) {
            throw new PortalException("RESCHEDULING_NOT_ALLOWED: This session does not allow rescheduling");
        }
        
        // Check if rescheduling is within allowed time frame
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionTime = session.getScheduledDateTime();
        long hoursUntilSession = java.time.Duration.between(now, sessionTime).toHours();
        
        if (hoursUntilSession < session.getMaxReschedulingHours()) {
            throw new PortalException("RESCHEDULING_TOO_LATE: Cannot reschedule within " + 
                session.getMaxReschedulingHours() + " hours of the session");
        }
        
        // Check for conflicts at new time
        List<TrialSessionDTO> conflicts = getConflictingSessions(session.getMentorId(), newDateTime, 
            session.getDurationMinutes(), session.getBufferTimeMinutes());
        
        if (!conflicts.isEmpty()) {
            throw new PortalException("TIME_SLOT_CONFLICT: The requested time slot conflicts with existing sessions");
        }
        
        // Update session
        session.setScheduledDateTime(newDateTime);
        session.setNotes(session.getNotes() + "\nRescheduled: " + reason);
        session.setUpdatedAt(LocalDateTime.now());
        
        return trialSessionRepository.save(session).toDTO();
    }

    @Override
    public List<TrialSessionDTO> findAlternativeSlots(Long originalSessionId, LocalDateTime preferredDateTime, 
            Integer durationHours) throws PortalException {
        TrialSession originalSession = trialSessionRepository.findById(originalSessionId)
                .orElseThrow(() -> new PortalException("TRIAL_SESSION_NOT_FOUND"));
        
        LocalDateTime startSearch = preferredDateTime.minusHours(durationHours);
        LocalDateTime endSearch = preferredDateTime.plusHours(durationHours);
        
        return trialSessionRepository.findByMentorIdAndScheduledDateTimeBetween(
                originalSession.getMentorId(), startSearch, endSearch)
                .stream()
                .filter(session -> session.getStatus() == TrialSessionStatus.AVAILABLE)
                .filter(session -> !session.getId().equals(originalSessionId))
                .map(TrialSession::toDTO)
                .collect(Collectors.toList());
    }
}
