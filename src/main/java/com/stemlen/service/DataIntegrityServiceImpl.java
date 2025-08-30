package com.stemlen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.entity.MentorshipPackage;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorRepository;
import com.stemlen.repository.MentorshipPackageRepository;

@Service("dataIntegrityService")
public class DataIntegrityServiceImpl implements DataIntegrityService {
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Autowired
    private MentorshipPackageRepository packageRepository;
    
    @Override
    public List<MentorshipPackageDTO> findOrphanedPackages() throws PortalException {
        List<MentorshipPackage> allPackages = packageRepository.findAll();
        List<MentorshipPackage> orphanedPackages = allPackages.stream()
                .filter(pkg -> {
                    // Check if the mentorId exists in the mentor collection
                    if (pkg.getMentorId() == null) {
                        return true; // Packages without mentorId are orphaned
                    }
                    return !mentorRepository.existsById(pkg.getMentorId());
                })
                .toList();
                
        return orphanedPackages.stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public boolean validateMentorExists(Long mentorId) throws PortalException {
        if (mentorId == null) {
            return false;
        }
        return mentorRepository.existsById(mentorId);
    }
    
    @Override
    public int fixOrphanedPackages(boolean deleteOrphaned) throws PortalException {
        List<MentorshipPackageDTO> orphanedPackages = findOrphanedPackages();
        int processedCount = 0;
        
        for (MentorshipPackageDTO packageDTO : orphanedPackages) {
            if (deleteOrphaned) {
                // Delete orphaned package
                packageRepository.deleteById(packageDTO.getId());
                processedCount++;
                System.out.println("🗑️  Deleted orphaned package: " + packageDTO.getPackageName() + " (ID: " + packageDTO.getId() + ")");
            } else {
                // Deactivate orphaned package instead of deleting
                Optional<MentorshipPackage> packageOpt = packageRepository.findById(packageDTO.getId());
                if (packageOpt.isPresent()) {
                    MentorshipPackage pkg = packageOpt.get();
                    pkg.setIsActive(false);
                    packageRepository.save(pkg);
                    processedCount++;
                    System.out.println("⚠️  Deactivated orphaned package: " + packageDTO.getPackageName() + " (ID: " + packageDTO.getId() + ")");
                }
            }
        }
        
        return processedCount;
    }
    
    @Override
    public String generateIntegrityReport() throws PortalException {
        StringBuilder report = new StringBuilder();
        report.append("=== MENTOR DATA INTEGRITY REPORT ===\n\n");
        
        // Count total entities
        long totalMentors = mentorRepository.count();
        long totalPackages = packageRepository.count();
        
        report.append("📊 Database Statistics:\n");
        report.append("   - Total Mentors: ").append(totalMentors).append("\n");
        report.append("   - Total Packages: ").append(totalPackages).append("\n\n");
        
        // Check for orphaned packages
        List<MentorshipPackageDTO> orphanedPackages = findOrphanedPackages();
        report.append("🔍 Orphaned Packages: ").append(orphanedPackages.size()).append("\n");
        if (!orphanedPackages.isEmpty()) {
            report.append("   Issues found:\n");
            for (MentorshipPackageDTO pkg : orphanedPackages) {
                report.append("   - Package '").append(pkg.getPackageName())
                      .append("' (ID: ").append(pkg.getId())
                      .append(") references non-existent mentor ID: ").append(pkg.getMentorId()).append("\n");
            }
        } else {
            report.append("   ✅ All packages have valid mentor references\n");
        }
        report.append("\n");
        
        // Summary
        int totalIssues = orphanedPackages.size();
        if (totalIssues == 0) {
            report.append("🎉 EXCELLENT! No data integrity issues found.\n");
        } else {
            report.append("⚠️  ATTENTION REQUIRED! Found ").append(totalIssues).append(" data integrity issues.\n");
            report.append("   - Recommendation: Run autoFixIntegrityIssues() to resolve automatically\n");
        }
        
        report.append("\n=== END OF REPORT ===");
        
        return report.toString();
    }
    
    @Override
    public String autoFixIntegrityIssues() throws PortalException {
        StringBuilder summary = new StringBuilder();
        summary.append("=== AUTO-FIX INTEGRITY ISSUES ===\n\n");
        
        // Fix orphaned packages (deactivate instead of delete to preserve data)
        int packagesFixed = fixOrphanedPackages(false);
        summary.append("📦 Packages processed: ").append(packagesFixed).append(" (deactivated)\n");
        
        int totalFixed = packagesFixed;
        summary.append("\n");
        
        if (totalFixed == 0) {
            summary.append("✅ No issues found - data integrity is already good!\n");
        } else {
            summary.append("🔧 Total issues resolved: ").append(totalFixed).append("\n");
            summary.append("   - Orphaned packages have been deactivated\n");
            summary.append("   - Data preserved for future manual review if needed\n");
        }
        
        summary.append("\n=== AUTO-FIX COMPLETE ===");
        
        return summary.toString();
    }
}