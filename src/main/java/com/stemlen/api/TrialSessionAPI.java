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
import com.stemlen.exception.PortalException;
import com.stemlen.service.TrialSessionService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/trial-sessions")
public class TrialSessionAPI {
    
    @Autowired
    private TrialSessionService trialSessionService;
    
    // Create available trial session slot
    @PostMapping("/create-slot")
    public ResponseEntity<TrialSessionDTO> createAvailableSlot(@RequestBody @Valid TrialSessionDTO trialSessionDTO) throws PortalException {
        return new ResponseEntity<>(trialSessionService.createAvailableSlot(trialSessionDTO), HttpStatus.CREATED);
    }
    
    // Create multiple available slots for a mentor
    @PostMapping("/create-multiple-slots")
    public ResponseEntity<List<TrialSessionDTO>> createMultipleAvailableSlots(
            @RequestParam Long mentorId,
            @RequestBody List<LocalDateTime> dateTimeSlots,
            @RequestParam(defaultValue = "30") Integer durationMinutes) throws PortalException {
        return new ResponseEntity<>(trialSessionService.createMultipleAvailableSlots(mentorId, dateTimeSlots, durationMinutes), HttpStatus.CREATED);
    }
    
    // Book a trial session
    @PostMapping("/book/{sessionId}")
    public ResponseEntity<TrialSessionDTO> bookTrialSession(
            @PathVariable Long sessionId,
            @RequestParam String menteeEmail,
            @RequestParam String menteeName,
            @RequestParam(required = false) String menteePhone) throws PortalException {
        return new ResponseEntity<>(trialSessionService.bookTrialSession(sessionId, menteeEmail, menteeName, menteePhone), HttpStatus.OK);
    }
    
    // Get trial session by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<TrialSessionDTO> getTrialSession(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(trialSessionService.getTrialSession(id), HttpStatus.OK);
    }
    
    // Get available trial sessions for a mentor
    @GetMapping("/mentor/{mentorId}/available")
    public ResponseEntity<List<TrialSessionDTO>> getAvailableSessionsByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(trialSessionService.getAvailableSessionsByMentor(mentorId), HttpStatus.OK);
    }
    
    // Get all trial sessions for a mentor
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<TrialSessionDTO>> getTrialSessionsByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(trialSessionService.getTrialSessionsByMentor(mentorId), HttpStatus.OK);
    }
    
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
    
    // Update trial session status
    @PutMapping("/update-status/{id}")
    public ResponseEntity<TrialSessionDTO> updateTrialSessionStatus(
            @PathVariable Long id, 
            @RequestParam TrialSessionStatus status) throws PortalException {
        return new ResponseEntity<>(trialSessionService.updateTrialSessionStatus(id, status), HttpStatus.OK);
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
    
    // Delete trial session
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteTrialSession(@PathVariable Long id) throws PortalException {
        trialSessionService.deleteTrialSession(id);
        return new ResponseEntity<>(new ResponseDTO("Trial session deleted successfully"), HttpStatus.OK);
    }
}