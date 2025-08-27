package com.stemlen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.stemlen.entity.MentorshipPackage;

@Repository
public interface MentorshipPackageRepository extends MongoRepository<MentorshipPackage, Long> {
    
    // Find packages by mentor ID
    List<MentorshipPackage> findByMentorId(Long mentorId);
    
    // Find active packages by mentor ID
    List<MentorshipPackage> findByMentorIdAndIsActive(Long mentorId, Boolean isActive);
    
    // Find packages by duration
    List<MentorshipPackage> findByDurationMonths(Integer durationMonths);
    
    // Find packages with free trial
    List<MentorshipPackage> findByIsFreeTrialIncluded(Boolean isFreeTrialIncluded);
    
    // Find packages within price range
    @Query("{ 'totalPrice' : { $gte: ?0, $lte: ?1 } }")
    List<MentorshipPackage> findByTotalPriceBetween(Double minPrice, Double maxPrice);
    
    // Find active packages
    List<MentorshipPackage> findByIsActive(Boolean isActive);
    
    // Find packages by session type
    List<MentorshipPackage> findBySessionType(String sessionType);
    
    // Find packages by mentor and duration
    Optional<MentorshipPackage> findByMentorIdAndDurationMonths(Long mentorId, Integer durationMonths);
}