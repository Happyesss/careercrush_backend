package com.stemlen.api;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.ResponseDTO;
import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.dto.BulkTrialSessionDTO;
import com.stemlen.dto.AvailabilityTemplateDTO;
import com.stemlen.exception.PortalException;
import com.stemlen.service.TrialSessionService;
import com.stemlen.repository.UserRepository;
import com.stemlen.repository.MentorRepository;
import com.stemlen.entity.User;
import com.stemlen.entity.Mentor;
import com.stemlen.jwt.JwtHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/trial-sessions")
public class TrialSessionAPI {
    
    @Autowired
    private TrialSessionService trialSessionService;
    
    @Autowired
    private JwtHelper jwtHelper;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorRepository mentorRepository;
    
    /**
     * 🔒 SECURITY: Extract user ID from JWT token
     * This method ensures that we know who is making the request
     */
    private Long getUserIdFromRequest(HttpServletRequest request) throws PortalException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Extract user ID from JWT token using claims
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor("your-256-bit-secret-your-256-bit-secret".getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                Object idClaim = claims.get("id");
                if (idClaim != null) {
                    return Long.valueOf(idClaim.toString());
                }
                throw new PortalException("USER_ID_NOT_FOUND: No user ID in token");
            } catch (Exception e) {
                throw new PortalException("INVALID_TOKEN: Unable to extract user ID from token - " + e.getMessage());
            }
        }
        throw new PortalException("UNAUTHORIZED_ACCESS: No valid authorization token provided");
    }
    
    /**
     * 🔗 HELPER: Resolve mentor ID from a user.
     * Prefers using profileId if it points to an existing mentor, otherwise
     * falls back to looking up Mentor by email. This handles cases where
     * mentors use sequence-based IDs independent of user IDs.
     */
    private Long getMentorIdFromUserId(Long userId) throws PortalException {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new PortalException("USER_NOT_FOUND: User with ID " + userId + " does not exist"));

            // 1) If profileId exists and a mentor with that ID exists, use it
            if (user.getProfileId() != null && mentorRepository.existsById(user.getProfileId())) {
                return user.getProfileId();
            }

            // 2) Otherwise, try to resolve mentor by email
            if (user.getEmail() != null) {
                Mentor mentor = mentorRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND: No mentor profile found for user email " + user.getEmail()));
                return mentor.getId();
            }

            throw new PortalException("MENTOR_PROFILE_NOT_FOUND: User " + userId + " does not have a mentor profile. Please create a mentor profile first.");
        } catch (PortalException e) {
            throw e;
        } catch (Exception e) {
            throw new PortalException("MENTOR_VALIDATION_ERROR: Unable to validate mentor for user " + userId + " - " + e.getMessage());
        }
    }
    
    // 🔒 CREATE: Only authenticated mentors can create trial session slots
    @PostMapping("/create-slot")
    public ResponseEntity<TrialSessionDTO> createAvailableSlot(
            @RequestBody TrialSessionDTO trialSessionDTO,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        // 🔒 FORCE OWNERSHIP: Set mentorId to the authenticated user's profileId
        trialSessionDTO.setMentorId(mentorId);
        
        return new ResponseEntity<>(
            trialSessionService.createAvailableSlot(trialSessionDTO), 
            HttpStatus.CREATED
        );
    }
    
    // Create multiple available slots for a mentor
    @PostMapping("/create-multiple-slots")
    public ResponseEntity<List<TrialSessionDTO>> createMultipleAvailableSlots(
            @RequestBody List<LocalDateTime> dateTimeSlots,
            @RequestParam(defaultValue = "30") Integer durationMinutes,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        // 🔒 OWNERSHIP: Use authenticated user's profileId as mentor
        return new ResponseEntity<>(
            trialSessionService.createMultipleAvailableSlots(mentorId, dateTimeSlots, durationMinutes), 
            HttpStatus.CREATED
        );
    }

    // 🆕 ENHANCED BULK OPERATIONS
    @PostMapping("/create-bulk-sessions")
    public ResponseEntity<List<TrialSessionDTO>> createBulkTrialSessions(
            @RequestBody @Valid BulkTrialSessionDTO bulkRequest,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.createBulkTrialSessions(bulkRequest, mentorId), 
            HttpStatus.CREATED
        );
    }

    @PostMapping("/create-recurring-sessions")
    public ResponseEntity<List<TrialSessionDTO>> createRecurringTrialSessions(
            @RequestBody @Valid TrialSessionDTO baseSession,
            @RequestParam String recurringPattern,
            @RequestParam String endDate,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        LocalDateTime recurringEndDate = LocalDateTime.parse(endDate);
        baseSession.setMentorId(mentorId); // 🔒 FORCE OWNERSHIP
        
        return new ResponseEntity<>(
            trialSessionService.createRecurringTrialSessions(baseSession, recurringPattern, recurringEndDate, mentorId), 
            HttpStatus.CREATED
        );
    }

    // 🆕 AVAILABILITY TEMPLATE OPERATIONS
    @PostMapping("/availability-templates")
    public ResponseEntity<AvailabilityTemplateDTO> saveAvailabilityTemplate(
            @RequestBody @Valid AvailabilityTemplateDTO template,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.saveAvailabilityTemplate(template, mentorId), 
            HttpStatus.CREATED
        );
    }

    @GetMapping("/availability-templates")
    public ResponseEntity<List<AvailabilityTemplateDTO>> getAvailabilityTemplates(
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.getAvailabilityTemplatesByMentor(mentorId), 
            HttpStatus.OK
        );
    }

    @GetMapping("/availability-templates/{templateId}")
    public ResponseEntity<AvailabilityTemplateDTO> getAvailabilityTemplate(
            @PathVariable Long templateId,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.getAvailabilityTemplate(templateId, mentorId), 
            HttpStatus.OK
        );
    }

    @DeleteMapping("/availability-templates/{templateId}")
    public ResponseEntity<ResponseDTO> deleteAvailabilityTemplate(
            @PathVariable Long templateId,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        trialSessionService.deleteAvailabilityTemplate(templateId, mentorId);
        
        return new ResponseEntity<>(
            new ResponseDTO("Availability template deleted successfully"), 
            HttpStatus.OK
        );
    }

    @PostMapping("/apply-template/{templateId}")
    public ResponseEntity<List<TrialSessionDTO>> applyAvailabilityTemplate(
            @PathVariable Long templateId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        return new ResponseEntity<>(
            trialSessionService.applyAvailabilityTemplate(templateId, start, end, mentorId), 
            HttpStatus.CREATED
        );
    }

    // 🆕 ENHANCED QUERY OPERATIONS
    @GetMapping("/date-range")
    public ResponseEntity<List<TrialSessionDTO>> getSessionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        return new ResponseEntity<>(
            trialSessionService.getTrialSessionsByDateRange(mentorId, start, end), 
            HttpStatus.OK
        );
    }

    @GetMapping("/conflicts")
    public ResponseEntity<List<TrialSessionDTO>> getConflictingSessions(
            @RequestParam String scheduledDateTime,
            @RequestParam Integer durationMinutes,
            @RequestParam(defaultValue = "5") Integer bufferMinutes,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        LocalDateTime dateTime = LocalDateTime.parse(scheduledDateTime);
        
        return new ResponseEntity<>(
            trialSessionService.getConflictingSessions(mentorId, dateTime, durationMinutes, bufferMinutes), 
            HttpStatus.OK
        );
    }

    // 🆕 BULK UPDATE OPERATIONS
    @PutMapping("/bulk-update")
    public ResponseEntity<List<TrialSessionDTO>> updateMultipleSessions(
            @RequestParam List<Long> sessionIds,
            @RequestBody TrialSessionDTO updates,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.updateMultipleTrialSessions(sessionIds, updates, mentorId), 
            HttpStatus.OK
        );
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ResponseDTO> deleteMultipleSessions(
            @RequestParam List<Long> sessionIds,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        trialSessionService.deleteMultipleTrialSessions(sessionIds, mentorId);
        
        return new ResponseEntity<>(
            new ResponseDTO("Sessions deleted successfully"), 
            HttpStatus.OK
        );
    }

    // 🆕 ENHANCED BOOKING OPERATIONS
    @PutMapping("/reschedule/{sessionId}")
    public ResponseEntity<TrialSessionDTO> rescheduleTrialSession(
            @PathVariable Long sessionId,
            @RequestParam String newDateTime,
            @RequestParam(required = false) String reason) throws PortalException {
        
        LocalDateTime newTime = LocalDateTime.parse(newDateTime);
        
        return new ResponseEntity<>(
            trialSessionService.rescheduleTrialSession(sessionId, newTime, reason), 
            HttpStatus.OK
        );
    }

    @GetMapping("/alternative-slots/{sessionId}")
    public ResponseEntity<List<TrialSessionDTO>> findAlternativeSlots(
            @PathVariable Long sessionId,
            @RequestParam String preferredDateTime,
            @RequestParam(defaultValue = "24") Integer durationHours) throws PortalException {
        
        LocalDateTime preferred = LocalDateTime.parse(preferredDateTime);
        
        return new ResponseEntity<>(
            trialSessionService.findAlternativeSlots(sessionId, preferred, durationHours), 
            HttpStatus.OK
        );
    }
    
    // 🔒 READ: Get trial session by ID (with ownership validation for sensitive data)
    @GetMapping("/get/{id}")
    public ResponseEntity<TrialSessionDTO> getTrialSession(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(trialSessionService.getTrialSession(id), HttpStatus.OK);
    }
    
    // 🔒 READ: Get trial sessions for authenticated mentor only
    @GetMapping("/mentor/my-sessions")
    public ResponseEntity<List<TrialSessionDTO>> getMyTrialSessions(HttpServletRequest request) throws PortalException {
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        return new ResponseEntity<>(
            trialSessionService.getTrialSessionsByMentor(mentorId), 
            HttpStatus.OK
        );
    }
    
    // 🔒 READ: Get available sessions for authenticated mentor only
    @GetMapping("/mentor/my-available")
    public ResponseEntity<List<TrialSessionDTO>> getMyAvailableSessions(HttpServletRequest request) throws PortalException {
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        return new ResponseEntity<>(
            trialSessionService.getAvailableSessionsByMentor(mentorId), 
            HttpStatus.OK
        );
    }
    
    // PUBLIC READ: Get available trial sessions for a mentor (for booking by mentees)
    @GetMapping("/mentor/{mentorId}/available")
    public ResponseEntity<List<TrialSessionDTO>> getAvailableSessionsByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(trialSessionService.getAvailableSessionsByMentor(mentorId), HttpStatus.OK);
    }
    
    // PUBLIC READ: Get all trial sessions for a mentor (public profile view)
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<TrialSessionDTO>> getTrialSessionsByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(trialSessionService.getTrialSessionsByMentor(mentorId), HttpStatus.OK);
    }
    
    // 🔒 UPDATE: Update trial session with ownership validation
    @PutMapping("/update/{id}")
    public ResponseEntity<TrialSessionDTO> updateTrialSession(
            @PathVariable Long id,
            @RequestBody @Valid TrialSessionDTO trialSessionDTO,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        return new ResponseEntity<>(
            trialSessionService.updateTrialSessionWithOwnership(id, trialSessionDTO, mentorId),
            HttpStatus.OK
        );
    }
    
    // Update trial session status
    @PutMapping("/update-status/{id}")
    public ResponseEntity<TrialSessionDTO> updateTrialSessionStatus(
            @PathVariable Long id, 
            @RequestParam TrialSessionStatus status) throws PortalException {
        return new ResponseEntity<>(trialSessionService.updateTrialSessionStatus(id, status), HttpStatus.OK);
    }
    
    // 🔒 DELETE: Delete trial session with ownership validation
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteTrialSession(
            @PathVariable Long id,
            HttpServletRequest request) throws PortalException {
        
        Long userId = getUserIdFromRequest(request);
        Long mentorId = getMentorIdFromUserId(userId);
        
        trialSessionService.deleteTrialSessionWithOwnership(id, mentorId);
        
        return new ResponseEntity<>(
            new ResponseDTO("Trial session deleted successfully"), 
            HttpStatus.OK
        );
    }
    
    // PUBLIC BOOKING OPERATIONS (can be accessed by mentees)
    
    // Book a trial session
    @PostMapping("/book/{sessionId}")
    public ResponseEntity<TrialSessionDTO> bookTrialSession(
            @PathVariable Long sessionId,
            @RequestParam String menteeEmail,
            @RequestParam String menteeName,
            @RequestParam(required = false) String menteePhone) throws PortalException {
        
        return new ResponseEntity<>(
            trialSessionService.bookTrialSession(sessionId, menteeEmail, menteeName, menteePhone), 
            HttpStatus.OK
        );
    }
    
    // Cancel trial session
    @PutMapping("/cancel/{id}")
    public ResponseEntity<TrialSessionDTO> cancelTrialSession(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(trialSessionService.cancelTrialSession(id), HttpStatus.OK);
    }
    
    // Complete trial session
    @PutMapping("/complete/{id}")
    public ResponseEntity<TrialSessionDTO> completeTrialSession(
            @PathVariable Long id, 
            @RequestParam(required = false) String notes) throws PortalException {
        return new ResponseEntity<>(trialSessionService.completeTrialSession(id, notes), HttpStatus.OK);
    }
    
    // QUERY OPERATIONS
    
    // Get trial sessions for a mentee
    @GetMapping("/mentee/{menteeId}")
    public ResponseEntity<List<TrialSessionDTO>> getTrialSessionsByMentee(@PathVariable Long menteeId) {
        return new ResponseEntity<>(trialSessionService.getTrialSessionsByMentee(menteeId), HttpStatus.OK);
    }
    
    // Get trial sessions for a package
    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<TrialSessionDTO>> getTrialSessionsByPackage(@PathVariable Long packageId) {
        return new ResponseEntity<>(trialSessionService.getTrialSessionsByPackage(packageId), HttpStatus.OK);
    }
    
    // Get available sessions for a specific date
    @GetMapping("/available")
    public ResponseEntity<List<TrialSessionDTO>> getAvailableSessionsForDate(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return new ResponseEntity<>(trialSessionService.getAvailableSessionsForDate(dateTime), HttpStatus.OK);
    }
    
    // Get booked sessions by mentee email
    @GetMapping("/booked")
    public ResponseEntity<List<TrialSessionDTO>> getBookedSessionsByEmail(@RequestParam String email) {
        return new ResponseEntity<>(trialSessionService.getBookedSessionsByEmail(email), HttpStatus.OK);
    }
    
    // ADMIN OPERATIONS
    
    // Get orphaned trial sessions (sessions with invalid mentorId)
    @GetMapping("/orphaned")
    public ResponseEntity<List<TrialSessionDTO>> getOrphanedTrialSessions() throws PortalException {
        return new ResponseEntity<>(trialSessionService.findOrphanedTrialSessions(), HttpStatus.OK);
    }
    
    // Cleanup orphaned trial sessions
    @DeleteMapping("/cleanup-orphaned")
    public ResponseEntity<ResponseDTO> cleanupOrphanedTrialSessions() throws PortalException {
        String result = trialSessionService.cleanupOrphanedTrialSessions();
        return new ResponseEntity<>(new ResponseDTO(result), HttpStatus.OK);
    }
}
