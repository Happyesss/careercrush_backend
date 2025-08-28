package com.stemlen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.stemlen.entity.Mentor;
import com.stemlen.dto.MentorshipStatus;

public interface MentorRepository extends MongoRepository<Mentor, Long> {
    
    // Find mentors by availability status
    List<Mentor> findByIsAvailable(Boolean isAvailable);
    
    // Find mentors by mentorship status
    List<Mentor> findByMentorshipStatus(MentorshipStatus mentorshipStatus);
    
    // Find mentors by expertise area
    @Query("{'mentorshipAreas': {$in: [?0]}}")
    List<Mentor> findByMentorshipAreasContaining(String expertise);
    
    // Find mentors by skills
    @Query("{'skills': {$in: [?0]}}")
    List<Mentor> findBySkillsContaining(String skill);
    
    // Find mentors by location
    List<Mentor> findByLocationContainingIgnoreCase(String location);
    
    // Find mentor by email
    Optional<Mentor> findByEmail(String email);
    
    // Find mentors with availability
    @Query("{'isAvailable': true}")
    List<Mentor> findAvailableMentors();
}