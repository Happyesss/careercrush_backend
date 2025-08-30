package com.stemlen.service;

import java.util.List;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.exception.PortalException;

public interface DataIntegrityService {
    
    /**
     * Find all mentorship packages with invalid mentor IDs
     * @return List of packages with invalid mentor references
     * @throws PortalException if operation fails
     */
    List<MentorshipPackageDTO> findOrphanedPackages() throws PortalException;
    
    /**
     * Validate mentor ID exists before creating/updating packages or sessions
     * @param mentorId The mentor ID to validate
     * @return true if mentor exists, false otherwise
     * @throws PortalException if validation fails
     */
    boolean validateMentorExists(Long mentorId) throws PortalException;
    
    /**
     * Fix orphaned packages by either deleting them or reassigning to valid mentors
     * @param deleteOrphaned true to delete orphaned packages, false to attempt reassignment
     * @return count of packages processed
     * @throws PortalException if operation fails
     */
    int fixOrphanedPackages(boolean deleteOrphaned) throws PortalException;
    
    /**
     * Perform comprehensive data integrity check and generate report
     * @return comprehensive integrity report
     * @throws PortalException if operation fails
     */
    String generateIntegrityReport() throws PortalException;
    
    /**
     * Auto-fix all data integrity issues
     * @return summary of fixes applied
     * @throws PortalException if operation fails
     */
    String autoFixIntegrityIssues() throws PortalException;
}