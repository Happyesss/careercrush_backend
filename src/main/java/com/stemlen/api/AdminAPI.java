package com.stemlen.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.ResponseDTO;
import com.stemlen.utility.Utilities;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminAPI {

    @Autowired
    private Utilities utilities;

    /**
     * Initialize sequences for the application
     */
    @PostMapping("/init-sequences")
    public ResponseEntity<ResponseDTO> initializeSequences() {
        try {
            // Initialize all required sequences
            String[] sequenceKeys = {"users", "profiles", "jobs", "applications"};
            
            for (String key : sequenceKeys) {
                Utilities.initializeSequence(key, 0L);
            }
            
            return new ResponseEntity<>(
                new ResponseDTO("✅ All sequences initialized successfully!"), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ResponseDTO("❌ Failed to initialize sequences: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Initialize a specific sequence
     */
    @PostMapping("/init-sequence/{key}")
    public ResponseEntity<ResponseDTO> initializeSequence(
            @PathVariable String key,
            @RequestParam(defaultValue = "0") Long initialValue) {
        try {
            Utilities.initializeSequence(key, initialValue);
            return new ResponseEntity<>(
                new ResponseDTO("✅ Sequence '" + key + "' initialized with value: " + initialValue), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ResponseDTO("❌ Failed to initialize sequence '" + key + "': " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ResponseDTO> healthCheck() {
        return new ResponseEntity<>(
            new ResponseDTO("✅ Stemlen Backend is running successfully!"), 
            HttpStatus.OK
        );
    }
}
