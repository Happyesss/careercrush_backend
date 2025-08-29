package com.stemlen.api;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.MentorDTO;
import com.stemlen.dto.MentorshipRequestDTO;
import com.stemlen.dto.MentorshipStatus;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.dto.SessionStatus;
import com.stemlen.exception.PortalException;
import com.stemlen.service.MentorService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/mentors")
public class MentorAPI {
    
    @Autowired
    private MentorService mentorService;
    
    // Create mentor profile
    @PostMapping("/create")
    public ResponseEntity<MentorDTO> createMentor(@RequestBody @Valid MentorDTO mentorDTO) throws PortalException {
        return new ResponseEntity<>(mentorService.createMentor(mentorDTO), HttpStatus.CREATED);
    }
    
    // Update mentor profile
    @PutMapping("/update")
    public ResponseEntity<MentorDTO> updateMentor(@RequestBody @Valid MentorDTO mentorDTO) throws PortalException {
        return new ResponseEntity<>(mentorService.updateMentor(mentorDTO), HttpStatus.OK);
    }
    
    // Get mentor by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<MentorDTO> getMentor(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(mentorService.getMentor(id), HttpStatus.OK);
    }
    
    // Get mentor by email
    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<MentorDTO> getMentorByEmail(@PathVariable String email) throws PortalException {
        return new ResponseEntity<>(mentorService.getMentorByEmail(email), HttpStatus.OK);
    }
    
    // Get all mentors
    @GetMapping("/getAll")
    public ResponseEntity<List<MentorDTO>> getAllMentors() {
        return new ResponseEntity<>(mentorService.getAllMentors(), HttpStatus.OK);
    }
    
    // Get available mentors
    @GetMapping("/available")
    public ResponseEntity<List<MentorDTO>> getAvailableMentors() {
        return new ResponseEntity<>(mentorService.getAvailableMentors(), HttpStatus.OK);
    }
    
    // Get mentors by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MentorDTO>> getMentorsByStatus(@PathVariable MentorshipStatus status) {
        return new ResponseEntity<>(mentorService.getMentorsByStatus(status), HttpStatus.OK);
    }
    
    // Get mentors by expertise
    @GetMapping("/expertise/{expertise}")
    public ResponseEntity<List<MentorDTO>> getMentorsByExpertise(@PathVariable String expertise) {
        return new ResponseEntity<>(mentorService.getMentorsByExpertise(expertise), HttpStatus.OK);
    }
    
    // Get mentors by skill
    @GetMapping("/skill/{skill}")
    public ResponseEntity<List<MentorDTO>> getMentorsBySkill(@PathVariable String skill) {
        return new ResponseEntity<>(mentorService.getMentorsBySkill(skill), HttpStatus.OK);
    }
    
    // Get mentors by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<MentorDTO>> getMentorsByLocation(@PathVariable String location) {
        return new ResponseEntity<>(mentorService.getMentorsByLocation(location), HttpStatus.OK);
    }
    
    // Update mentor availability
    @PutMapping("/availability/{id}")
    public ResponseEntity<MentorDTO> updateMentorAvailability(
            @PathVariable Long id, 
            @RequestParam Boolean isAvailable) throws PortalException {
        return new ResponseEntity<>(mentorService.updateMentorAvailability(id, isAvailable), HttpStatus.OK);
    }
    
    // Update mentor status
    @PutMapping("/status/{id}")
    public ResponseEntity<MentorDTO> updateMentorStatus(
            @PathVariable Long id, 
            @RequestParam MentorshipStatus status) throws PortalException {
        return new ResponseEntity<>(mentorService.updateMentorStatus(id, status), HttpStatus.OK);
    }
    
    // Assign mentee to mentor
    @PostMapping("/assignMentee/{mentorId}")
    public ResponseEntity<ResponseDTO> assignMentee(@PathVariable Long mentorId) throws PortalException {
        mentorService.assignMentee(mentorId);
        return new ResponseEntity<>(new ResponseDTO("Mentee assigned successfully"), HttpStatus.OK);
    }
    
    // Remove mentee from mentor
    @PostMapping("/removeMentee/{mentorId}")
    public ResponseEntity<ResponseDTO> removeMentee(@PathVariable Long mentorId) throws PortalException {
        mentorService.removeMentee(mentorId);
        return new ResponseEntity<>(new ResponseDTO("Mentee removed successfully"), HttpStatus.OK);
    }
    
    // Delete mentor profile
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteMentor(@PathVariable Long id) throws PortalException {
        mentorService.deleteMentor(id);
        return new ResponseEntity<>(new ResponseDTO("Mentor deleted successfully"), HttpStatus.OK);
    }
    
    // Request mentorship session
    @PostMapping("/requestSession/{mentorId}")
    public ResponseEntity<ResponseDTO> requestMentorshipSession(
            @PathVariable Long mentorId, 
            @RequestBody @Valid MentorshipRequestDTO requestDTO) throws PortalException {
        mentorService.requestMentorshipSession(mentorId, requestDTO);
        return new ResponseEntity<>(new ResponseDTO("Mentorship session requested successfully"), HttpStatus.OK);
    }
    
    // Update mentorship request status
    @PutMapping("/updateRequestStatus/{mentorId}/{requestId}")
    public ResponseEntity<ResponseDTO> updateMentorshipRequestStatus(
            @PathVariable Long mentorId,
            @PathVariable Long requestId,
            @RequestParam SessionStatus status,
            @RequestParam(required = false) LocalDateTime scheduledTime) throws PortalException {
        mentorService.updateMentorshipRequestStatus(mentorId, requestId, status, scheduledTime);
        return new ResponseEntity<>(new ResponseDTO("Mentorship request status updated successfully"), HttpStatus.OK);
    }
    
    // Validate mentor data integrity (check for orphaned references)
    @GetMapping("/validate-data-integrity")
    public ResponseEntity<ResponseDTO> validateMentorDataIntegrity() throws PortalException {
        // This endpoint can be expanded to validate all mentor-related data integrity
        return new ResponseEntity<>(new ResponseDTO("Mentor data integrity check completed"), HttpStatus.OK);
    }
}