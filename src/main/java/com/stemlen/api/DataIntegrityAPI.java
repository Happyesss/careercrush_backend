package com.stemlen.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.exception.PortalException;
import com.stemlen.service.DataIntegrityService;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/data-integrity")
public class DataIntegrityAPI {
    
    @Autowired
    private DataIntegrityService dataIntegrityService;
    
    // Generate comprehensive data integrity report
    @GetMapping("/report")
    public ResponseEntity<ResponseDTO> generateIntegrityReport() throws PortalException {
        String report = dataIntegrityService.generateIntegrityReport();
        return new ResponseEntity<>(new ResponseDTO(report), HttpStatus.OK);
    }
    
    // Auto-fix all data integrity issues
    @PostMapping("/auto-fix")
    public ResponseEntity<ResponseDTO> autoFixIntegrityIssues() throws PortalException {
        String summary = dataIntegrityService.autoFixIntegrityIssues();
        return new ResponseEntity<>(new ResponseDTO(summary), HttpStatus.OK);
    }
    
    // Find all orphaned packages
    @GetMapping("/orphaned-packages")
    public ResponseEntity<List<MentorshipPackageDTO>> findOrphanedPackages() throws PortalException {
        return new ResponseEntity<>(dataIntegrityService.findOrphanedPackages(), HttpStatus.OK);
    }
    
    // Validate mentor exists
    @GetMapping("/validate-mentor/{mentorId}")
    public ResponseEntity<ResponseDTO> validateMentorExists(@PathVariable Long mentorId) throws PortalException {
        boolean exists = dataIntegrityService.validateMentorExists(mentorId);
        String message = exists ? "Mentor ID " + mentorId + " is valid" : "Mentor ID " + mentorId + " does not exist";
        return new ResponseEntity<>(new ResponseDTO(message), HttpStatus.OK);
    }
    
    // Fix orphaned packages
    @PostMapping("/fix-packages")
    public ResponseEntity<ResponseDTO> fixOrphanedPackages(@RequestParam(defaultValue = "false") boolean delete) throws PortalException {
        int count = dataIntegrityService.fixOrphanedPackages(delete);
        String action = delete ? "deleted" : "deactivated";
        String message = "Successfully " + action + " " + count + " orphaned packages";
        return new ResponseEntity<>(new ResponseDTO(message), HttpStatus.OK);
    }
}