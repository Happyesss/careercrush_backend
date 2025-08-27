package com.stemlen.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.MentorDTO;
import com.stemlen.dto.MentorshipRequestDTO;
import com.stemlen.dto.MentorshipStatus;
import com.stemlen.dto.SessionStatus;
import com.stemlen.entity.Mentor;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorRepository;
import com.stemlen.utility.Utilities;

@Service("mentorService")
public class MentorServiceImpl implements MentorService {
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Override
    public MentorDTO createMentor(MentorDTO mentorDTO) throws PortalException {
        if (Objects.isNull(mentorDTO.getId()) || mentorDTO.getId() == 0) {
            mentorDTO.setId(Utilities.getNextSequence("mentors"));
            mentorDTO.setJoinDate(LocalDateTime.now());
            
            // Set default values for new mentors
            if (mentorDTO.getCurrentMentees() == null) {
                mentorDTO.setCurrentMentees(0);
            }
            if (mentorDTO.getIsAvailable() == null) {
                mentorDTO.setIsAvailable(true);
            }
            if (mentorDTO.getMentorshipStatus() == null) {
                mentorDTO.setMentorshipStatus(MentorshipStatus.ACTIVE);
            }
        }
        
        Mentor mentor = mentorRepository.save(mentorDTO.toEntity());
        return mentor.toDTO();
    }
    
    @Override
    public MentorDTO updateMentor(MentorDTO mentorDTO) throws PortalException {
        mentorRepository.findById(mentorDTO.getId())
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        Mentor mentor = mentorRepository.save(mentorDTO.toEntity());
        return mentor.toDTO();
    }
    
    @Override
    public MentorDTO getMentor(Long id) throws PortalException {
        return mentorRepository.findById(id)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"))
                .toDTO();
    }
    
    @Override
    public MentorDTO getMentorByEmail(String email) throws PortalException {
        return mentorRepository.findByEmail(email)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"))
                .toDTO();
    }
    
    @Override
    public List<MentorDTO> getAllMentors() {
        return mentorRepository.findAll().stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getAvailableMentors() {
        return mentorRepository.findByIsAvailable(true).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsByStatus(MentorshipStatus status) {
        return mentorRepository.findByMentorshipStatus(status).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsByExpertise(String expertise) {
        return mentorRepository.findByMentorshipAreasContaining(expertise).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsBySkill(String skill) {
        return mentorRepository.findBySkillsContaining(skill).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsByLocation(String location) {
        return mentorRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsWithCapacity() {
        return mentorRepository.findAvailableMentorsWithCapacity().stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public List<MentorDTO> getMentorsByRateRange(Double minRate, Double maxRate) {
        return mentorRepository.findByHourlyRateBetween(minRate, maxRate).stream()
                .map(Mentor::toDTO)
                .toList();
    }
    
    @Override
    public MentorDTO updateMentorAvailability(Long id, Boolean isAvailable) throws PortalException {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        mentor.setIsAvailable(isAvailable);
        mentor = mentorRepository.save(mentor);
        return mentor.toDTO();
    }
    
    @Override
    public MentorDTO updateMentorStatus(Long id, MentorshipStatus status) throws PortalException {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        mentor.setMentorshipStatus(status);
        mentor = mentorRepository.save(mentor);
        return mentor.toDTO();
    }
    
    @Override
    public MentorDTO assignMentee(Long mentorId) throws PortalException {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        if (mentor.getCurrentMentees() >= mentor.getMaxMentees()) {
            throw new PortalException("MENTOR_AT_CAPACITY");
        }
        
        mentor.setCurrentMentees(mentor.getCurrentMentees() + 1);
        
        // Update availability status if at capacity
        if (mentor.getCurrentMentees().equals(mentor.getMaxMentees())) {
            mentor.setMentorshipStatus(MentorshipStatus.BUSY);
            mentor.setIsAvailable(false);
        }
        
        mentor = mentorRepository.save(mentor);
        return mentor.toDTO();
    }
    
    @Override
    public MentorDTO removeMentee(Long mentorId) throws PortalException {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        if (mentor.getCurrentMentees() <= 0) {
            throw new PortalException("NO_MENTEES_TO_REMOVE");
        }
        
        mentor.setCurrentMentees(mentor.getCurrentMentees() - 1);
        
        // Update availability status if below capacity
        if (mentor.getCurrentMentees() < mentor.getMaxMentees()) {
            if (mentor.getMentorshipStatus() == MentorshipStatus.BUSY) {
                mentor.setMentorshipStatus(MentorshipStatus.ACTIVE);
                mentor.setIsAvailable(true);
            }
        }
        
        mentor = mentorRepository.save(mentor);
        return mentor.toDTO();
    }
    
    @Override
    public void deleteMentor(Long id) throws PortalException {
        if (!mentorRepository.existsById(id)) {
            throw new PortalException("MENTOR_NOT_FOUND");
        }
        mentorRepository.deleteById(id);
    }
    
    @Override
    public void requestMentorshipSession(Long mentorId, MentorshipRequestDTO requestDTO) throws PortalException {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        // Set default values for the request
        requestDTO.setRequestId(Utilities.getNextSequence("mentorshipRequests"));
        requestDTO.setRequestTime(LocalDateTime.now());
        requestDTO.setSessionStatus(SessionStatus.REQUESTED);
        
        // Add the request to mentor's list
        if (mentor.getMentorshipRequests() == null) {
            mentor.setMentorshipRequests(new ArrayList<>());
        }
        mentor.getMentorshipRequests().add(requestDTO.toEntity());
        
        mentorRepository.save(mentor);
    }
    
    @Override
    public void updateMentorshipRequestStatus(Long mentorId, Long requestId, SessionStatus status, LocalDateTime scheduledTime) throws PortalException {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new PortalException("MENTOR_NOT_FOUND"));
        
        if (mentor.getMentorshipRequests() == null) {
            throw new PortalException("NO_MENTORSHIP_REQUESTS_FOUND");
        }
        
        // Update the specific request
        mentor.getMentorshipRequests().stream()
                .filter(request -> request.getRequestId().equals(requestId))
                .findFirst()
                .ifPresentOrElse(
                    request -> {
                        request.setSessionStatus(status);
                        if (scheduledTime != null) {
                            request.setScheduledTime(scheduledTime);
                        }
                    },
                    () -> {
                        throw new RuntimeException("MENTORSHIP_REQUEST_NOT_FOUND");
                    }
                );
        
        mentorRepository.save(mentor);
    }
}