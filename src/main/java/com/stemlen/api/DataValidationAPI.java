package com.stemlen.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.DataValidationReport;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.exception.PortalException;
import com.stemlen.service.DataValidationService;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/data-validation")
public class DataValidationAPI {
    
    @Autowired
    private DataValidationService dataValidationService;
    
    // Get comprehensive data integrity report
    @GetMapping("/report")
    public ResponseEntity<DataValidationReport> getDataIntegrityReport() throws PortalException {
        return new ResponseEntity<>(dataValidationService.getDataIntegrityReport(), HttpStatus.OK);
    }
    
    // Validate mentor-trial session sync
    @GetMapping("/mentor-trial-sessions")
    public ResponseEntity<DataValidationReport> validateMentorTrialSessionSync() throws PortalException {
        return new ResponseEntity<>(dataValidationService.validateMentorTrialSessionSync(), HttpStatus.OK);
    }
    
    // Validate mentor-package sync
    @GetMapping("/mentor-packages")
    public ResponseEntity<DataValidationReport> validateMentorPackageSync() throws PortalException {
        return new ResponseEntity<>(dataValidationService.validateMentorPackageSync(), HttpStatus.OK);
    }
    
    // Fix orphaned trial sessions
    @PostMapping("/fix-orphaned-trial-sessions")
    public ResponseEntity<ResponseDTO> fixOrphanedTrialSessions() throws PortalException {
        String result = dataValidationService.fixOrphanedTrialSessions();
        return new ResponseEntity<>(new ResponseDTO(result), HttpStatus.OK);
    }
    
    // Fix orphaned mentorship packages
    @PostMapping("/fix-orphaned-packages")
    public ResponseEntity<ResponseDTO> fixOrphanedMentorshipPackages() throws PortalException {
        String result = dataValidationService.fixOrphanedMentorshipPackages();
        return new ResponseEntity<>(new ResponseDTO(result), HttpStatus.OK);
    }
    
    // Fix all data integrity issues
    @PostMapping("/fix-all")
    public ResponseEntity<ResponseDTO> fixAllDataIntegrityIssues() throws PortalException {
        String trialSessionResult = dataValidationService.fixOrphanedTrialSessions();
        String packageResult = dataValidationService.fixOrphanedMentorshipPackages();
        
        String combinedResult = trialSessionResult + "; " + packageResult;
        return new ResponseEntity<>(new ResponseDTO(combinedResult), HttpStatus.OK);
    }
}