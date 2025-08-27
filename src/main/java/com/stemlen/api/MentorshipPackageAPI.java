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
import com.stemlen.service.MentorshipPackageService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/packages")
public class MentorshipPackageAPI {
    
    @Autowired
    private MentorshipPackageService packageService;
    
    // Create a new mentorship package
    @PostMapping("/create")
    public ResponseEntity<MentorshipPackageDTO> createPackage(@RequestBody @Valid MentorshipPackageDTO packageDTO) throws PortalException {
        return new ResponseEntity<>(packageService.createPackage(packageDTO), HttpStatus.CREATED);
    }
    
    // Update an existing package
    @PutMapping("/update")
    public ResponseEntity<MentorshipPackageDTO> updatePackage(@RequestBody @Valid MentorshipPackageDTO packageDTO) throws PortalException {
        return new ResponseEntity<>(packageService.updatePackage(packageDTO), HttpStatus.OK);
    }
    
    // Get package by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<MentorshipPackageDTO> getPackage(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(packageService.getPackage(id), HttpStatus.OK);
    }
    
    // Get all packages for a mentor
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<MentorshipPackageDTO>> getPackagesByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(packageService.getPackagesByMentor(mentorId), HttpStatus.OK);
    }
    
    // Get active packages for a mentor
    @GetMapping("/mentor/{mentorId}/active")
    public ResponseEntity<List<MentorshipPackageDTO>> getActivePackagesByMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(packageService.getActivePackagesByMentor(mentorId), HttpStatus.OK);
    }
    
    // Get all active packages
    @GetMapping("/active")
    public ResponseEntity<List<MentorshipPackageDTO>> getAllActivePackages() {
        return new ResponseEntity<>(packageService.getAllActivePackages(), HttpStatus.OK);
    }
    
    // Get packages by duration
    @GetMapping("/duration/{months}")
    public ResponseEntity<List<MentorshipPackageDTO>> getPackagesByDuration(@PathVariable Integer months) {
        return new ResponseEntity<>(packageService.getPackagesByDuration(months), HttpStatus.OK);
    }
    
    // Get packages with free trial
    @GetMapping("/free-trial")
    public ResponseEntity<List<MentorshipPackageDTO>> getPackagesWithFreeTrial() {
        return new ResponseEntity<>(packageService.getPackagesWithFreeTrial(), HttpStatus.OK);
    }
    
    // Get packages within price range
    @GetMapping("/price-range")
    public ResponseEntity<List<MentorshipPackageDTO>> getPackagesByPriceRange(
            @RequestParam Double minPrice, 
            @RequestParam Double maxPrice) {
        return new ResponseEntity<>(packageService.getPackagesByPriceRange(minPrice, maxPrice), HttpStatus.OK);
    }
    
    // Get packages by session type
    @GetMapping("/session-type/{sessionType}")
    public ResponseEntity<List<MentorshipPackageDTO>> getPackagesBySessionType(@PathVariable String sessionType) {
        return new ResponseEntity<>(packageService.getPackagesBySessionType(sessionType), HttpStatus.OK);
    }
    
    // Toggle package status (activate/deactivate)
    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<MentorshipPackageDTO> togglePackageStatus(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(packageService.togglePackageStatus(id), HttpStatus.OK);
    }
    
    // Delete a package
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deletePackage(@PathVariable Long id) throws PortalException {
        packageService.deletePackage(id);
        return new ResponseEntity<>(new ResponseDTO("Package deleted successfully"), HttpStatus.OK);
    }
}