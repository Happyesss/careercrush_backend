package com.stemlen.service;

import com.stemlen.dto.DataValidationReport;
import com.stemlen.exception.PortalException;

public interface DataValidationService {
    
    // Validate all mentor-package relationships
    DataValidationReport validateMentorPackageSync() throws PortalException;
    
    // Comprehensive data integrity check
    DataValidationReport validateAllDataIntegrity() throws PortalException;
    
    // Fix orphaned mentorship packages
    String fixOrphanedMentorshipPackages() throws PortalException;
    
    // Get summary of all data integrity issues
    DataValidationReport getDataIntegrityReport() throws PortalException;
}