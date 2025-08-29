package com.stemlen.service;

import java.util.List;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.exception.PortalException;

public interface MentorshipPackageService {
    
    // Create a new mentorship package
    MentorshipPackageDTO createPackage(MentorshipPackageDTO packageDTO) throws PortalException;
    
    // Update an existing package
    MentorshipPackageDTO updatePackage(MentorshipPackageDTO packageDTO) throws PortalException;
    
    // Get package by ID
    MentorshipPackageDTO getPackage(Long id) throws PortalException;
    
    // Get all packages for a mentor
    List<MentorshipPackageDTO> getPackagesByMentor(Long mentorId);
    
    // Get active packages for a mentor
    List<MentorshipPackageDTO> getActivePackagesByMentor(Long mentorId);
    
    // Get all active packages
    List<MentorshipPackageDTO> getAllActivePackages();
    
    // Get packages by duration
    List<MentorshipPackageDTO> getPackagesByDuration(Integer durationMonths);
    
    // Get packages with free trial
    List<MentorshipPackageDTO> getPackagesWithFreeTrial();
    
    // Get packages within price range
    List<MentorshipPackageDTO> getPackagesByPriceRange(Double minPrice, Double maxPrice);
    
    // Delete a package
    void deletePackage(Long id) throws PortalException;
    
    // Activate/Deactivate package
    MentorshipPackageDTO togglePackageStatus(Long id) throws PortalException;
    
    // Get packages by session type
    List<MentorshipPackageDTO> getPackagesBySessionType(String sessionType);
    
    // Validate and cleanup orphaned packages (packages with invalid mentorId)
    List<MentorshipPackageDTO> findOrphanedPackages() throws PortalException;
    
    // Delete orphaned packages (packages with invalid mentorId)
    void cleanupOrphanedPackages() throws PortalException;
}