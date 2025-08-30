package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.DataValidationReport;
import com.stemlen.dto.DataValidationReport.ValidationIssue;
import com.stemlen.entity.MentorshipPackage;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorRepository;
import com.stemlen.repository.MentorshipPackageRepository;

@Service("dataValidationService")
public class DataValidationServiceImpl implements DataValidationService {
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Autowired
    private MentorshipPackageRepository packageRepository;
    
    @Override
    public DataValidationReport validateMentorPackageSync() throws PortalException {
        List<ValidationIssue> issues = new ArrayList<>();
        
        // Find packages with invalid mentor IDs
        List<MentorshipPackage> allPackages = packageRepository.findAll();
        for (MentorshipPackage pkg : allPackages) {
            if (pkg.getMentorId() == null) {
                issues.add(new ValidationIssue(
                    "MISSING_MENTOR_ID",
                    "MentorshipPackage",
                    pkg.getId(),
                    "Mentorship package has null mentorId",
                    "HIGH",
                    "Delete package or assign valid mentor ID"
                ));
            } else if (!mentorRepository.existsById(pkg.getMentorId())) {
                issues.add(new ValidationIssue(
                    "INVALID_MENTOR_REFERENCE",
                    "MentorshipPackage",
                    pkg.getId(),
                    "Mentorship package references non-existent mentor ID: " + pkg.getMentorId(),
                    "HIGH",
                    "Delete package or correct mentor ID"
                ));
            }
        }
        
        return createReport("MENTOR_PACKAGE_SYNC", issues);
    }
    
    @Override
    public DataValidationReport validateAllDataIntegrity() throws PortalException {
        List<ValidationIssue> allIssues = new ArrayList<>();
        
        // Combine all validation checks
        DataValidationReport packageReport = validateMentorPackageSync();
        
        allIssues.addAll(packageReport.getDetailedIssues());
        
        return createReport("COMPREHENSIVE_DATA_INTEGRITY", allIssues);
    }
    
    @Override
    public String fixOrphanedMentorshipPackages() throws PortalException {
        List<MentorshipPackage> allPackages = packageRepository.findAll();
        List<MentorshipPackage> toDelete = new ArrayList<>();
        
        for (MentorshipPackage pkg : allPackages) {
            if (pkg.getMentorId() == null || !mentorRepository.existsById(pkg.getMentorId())) {
                toDelete.add(pkg);
            }
        }
        
        packageRepository.deleteAll(toDelete);
        return "Deleted " + toDelete.size() + " orphaned mentorship packages";
    }
    
    @Override
    public DataValidationReport getDataIntegrityReport() throws PortalException {
        return validateAllDataIntegrity();
    }
    
    private DataValidationReport createReport(String reportType, List<ValidationIssue> issues) {
        DataValidationReport report = new DataValidationReport();
        report.setReportTimestamp(LocalDateTime.now());
        report.setReportType(reportType);
        report.setHasIssues(!issues.isEmpty());
        report.setTotalIssuesFound(issues.size());
        report.setDetailedIssues(issues);
        
        List<String> descriptions = issues.stream()
                .map(ValidationIssue::getDescription)
                .toList();
        report.setIssueDescriptions(descriptions);
        
        if (issues.isEmpty()) {
            report.setSummary("No data integrity issues found");
            report.setRecommendedActions("No action required");
        } else {
            report.setSummary(issues.size() + " data integrity issues found");
            report.setRecommendedActions("Review issues and run appropriate fix methods");
        }
        
        return report;
    }
}