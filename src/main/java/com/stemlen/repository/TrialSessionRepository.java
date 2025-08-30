package com.stemlen.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.entity.TrialSession;

public interface TrialSessionRepository extends MongoRepository<TrialSession, Long> {
    
    // 🔒 SECURITY: Find sessions by mentor - ensures ownership filtering
    List<TrialSession> findByMentorId(Long mentorId);
    
    // Find sessions by mentor and status
    List<TrialSession> findByMentorIdAndStatus(Long mentorId, TrialSessionStatus status);
    
    // Find sessions by mentee
    List<TrialSession> findByMenteeId(Long menteeId);
    
    // Find sessions by package
    List<TrialSession> findByPackageId(Long packageId);
    
    // Find available sessions for a specific mentor
    @Query("{'mentorId': ?0, 'status': 'AVAILABLE', 'scheduledDateTime': {$gte: ?1}}")
    List<TrialSession> findAvailableSessionsByMentorAfterDate(Long mentorId, LocalDateTime fromDate);
    
    // Find sessions by date range for a mentor
    @Query("{'mentorId': ?0, 'scheduledDateTime': {$gte: ?1, $lte: ?2}}")
    List<TrialSession> findByMentorIdAndScheduledDateTimeBetween(Long mentorId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find booked sessions by mentee email
    List<TrialSession> findByMenteeEmailAndStatus(String menteeEmail, TrialSessionStatus status);
    
    // Find sessions scheduled for today or future
    @Query("{'scheduledDateTime': {$gte: ?0}}")
    List<TrialSession> findFutureSessions(LocalDateTime fromDate);
    
    // 🔒 SECURITY: Validate ownership before any operation
    @Query("{'id': ?0, 'mentorId': ?1}")
    TrialSession findByIdAndMentorId(Long sessionId, Long mentorId);
}
