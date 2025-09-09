package com.stemlen.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.stemlen.entity.AvailabilityTemplate;

public interface AvailabilityTemplateRepository extends MongoRepository<AvailabilityTemplate, Long> {
    
    // Find templates by mentor
    List<AvailabilityTemplate> findByMentorId(Long mentorId);
    
    // Find active templates by mentor
    List<AvailabilityTemplate> findByMentorIdAndIsActiveTrue(Long mentorId);
    
    // Find default template for mentor
    AvailabilityTemplate findByMentorIdAndIsDefaultTrue(Long mentorId);
    
    // Find template by name and mentor
    AvailabilityTemplate findByMentorIdAndTemplateName(Long mentorId, String templateName);
    
    // Security: Find template by ID and mentor (ownership validation)
    @Query("{'id': ?0, 'mentorId': ?1}")
    AvailabilityTemplate findByIdAndMentorId(Long templateId, Long mentorId);
}
