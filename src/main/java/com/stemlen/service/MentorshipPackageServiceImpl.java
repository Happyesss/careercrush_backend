package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.entity.MentorshipPackage;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorshipPackageRepository;
import com.stemlen.utility.Utilities;

@Service("mentorshipPackageService")
public class MentorshipPackageServiceImpl implements MentorshipPackageService {
    
    @Autowired
    private MentorshipPackageRepository packageRepository;
    
    @Override
    public MentorshipPackageDTO createPackage(MentorshipPackageDTO packageDTO) throws PortalException {
        if (Objects.isNull(packageDTO.getId()) || packageDTO.getId() == 0) {
            packageDTO.setId(Utilities.getNextSequence("mentorshipPackages"));
            packageDTO.setCreatedAt(LocalDateTime.now());
        }
        packageDTO.setUpdatedAt(LocalDateTime.now());
        
        // Set default values if not provided
        if (packageDTO.getIsActive() == null) {
            packageDTO.setIsActive(true);
        }
        if (packageDTO.getIsFreeTrialIncluded() == null) {
            packageDTO.setIsFreeTrialIncluded(true);
        }
        if (packageDTO.getSessionDurationMinutes() == null) {
            packageDTO.setSessionDurationMinutes(60); // Default 1 hour sessions
        }
        
        // Set default package inclusions (as per Preplaced documentation)
        if (packageDTO.getHasUnlimitedChat() == null) {
            packageDTO.setHasUnlimitedChat(true);
        }
        if (packageDTO.getHasCuratedTasks() == null) {
            packageDTO.setHasCuratedTasks(true);
        }
        if (packageDTO.getHasRegularFollowups() == null) {
            packageDTO.setHasRegularFollowups(true);
        }
        if (packageDTO.getHasJobReferrals() == null) {
            packageDTO.setHasJobReferrals(true);
        }
        if (packageDTO.getHasCertification() == null) {
            packageDTO.setHasCertification(true);
        }
        if (packageDTO.getHasRescheduling() == null) {
            packageDTO.setHasRescheduling(true);
        }
        
        MentorshipPackage savedPackage = packageRepository.save(packageDTO.toEntity());
        return savedPackage.toDTO();
    }
    
    @Override
    public MentorshipPackageDTO updatePackage(MentorshipPackageDTO packageDTO) throws PortalException {
        packageRepository.findById(packageDTO.getId())
                .orElseThrow(() -> new PortalException("PACKAGE_NOT_FOUND"));
        
        packageDTO.setUpdatedAt(LocalDateTime.now());
        MentorshipPackage updatedPackage = packageRepository.save(packageDTO.toEntity());
        return updatedPackage.toDTO();
    }
    
    @Override
    public MentorshipPackageDTO getPackage(Long id) throws PortalException {
        return packageRepository.findById(id)
                .orElseThrow(() -> new PortalException("PACKAGE_NOT_FOUND"))
                .toDTO();
    }
    
    @Override
    public List<MentorshipPackageDTO> getPackagesByMentor(Long mentorId) {
        return packageRepository.findByMentorId(mentorId).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorshipPackageDTO> getActivePackagesByMentor(Long mentorId) {
        return packageRepository.findByMentorIdAndIsActive(mentorId, true).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorshipPackageDTO> getAllActivePackages() {
        return packageRepository.findByIsActive(true).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorshipPackageDTO> getPackagesByDuration(Integer durationMonths) {
        return packageRepository.findByDurationMonths(durationMonths).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorshipPackageDTO> getPackagesWithFreeTrial() {
        return packageRepository.findByIsFreeTrialIncluded(true).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorshipPackageDTO> getPackagesByPriceRange(Double minPrice, Double maxPrice) {
        return packageRepository.findByTotalPriceBetween(minPrice, maxPrice).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
    
    @Override
    public void deletePackage(Long id) throws PortalException {
        if (!packageRepository.existsById(id)) {
            throw new PortalException("PACKAGE_NOT_FOUND");
        }
        packageRepository.deleteById(id);
    }
    
    @Override
    public MentorshipPackageDTO togglePackageStatus(Long id) throws PortalException {
        MentorshipPackage packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new PortalException("PACKAGE_NOT_FOUND"));
        
        packageEntity.setIsActive(!packageEntity.getIsActive());
        packageEntity.setUpdatedAt(LocalDateTime.now());
        
        MentorshipPackage updatedPackage = packageRepository.save(packageEntity);
        return updatedPackage.toDTO();
    }
    
    @Override
    public List<MentorshipPackageDTO> getPackagesBySessionType(String sessionType) {
        return packageRepository.findBySessionType(sessionType).stream()
                .map(MentorshipPackage::toDTO)
                .toList();
    }
}