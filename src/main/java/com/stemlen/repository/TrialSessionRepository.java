package com.stemlen.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
    
    // 🆕 ENHANCED QUERIES FOR NEW FEATURES
    
    // Find sessions by parent session ID (for recurring sessions)
    List<TrialSession> findByParentSessionId(Long parentSessionId);
    
    // Find recurring sessions
    List<TrialSession> findByMentorIdAndIsRecurringTrue(Long mentorId);
    
    // Find sessions by availability template
    List<TrialSession> findByMentorIdAndAvailabilityTemplate(Long mentorId, String templateName);
    
    // Find conflicting sessions (overlapping times with buffer)
    @Query("{'mentorId': ?0, 'status': {$in: ['AVAILABLE', 'BOOKED']}, " +
           "'scheduledDateTime': {$gte: ?1, $lte: ?2}}")
    List<TrialSession> findConflictingSessions(Long mentorId, LocalDateTime startTime, LocalDateTime endTime);
    
    // Find sessions by time pattern (for recurring checks)
    @Query("{'mentorId': ?0, '$expr': {$and: [" +
           "{$gte: [{$hour: '$scheduledDateTime'}, ?1]}, " +
           "{$lte: [{$hour: '$scheduledDateTime'}, ?2]}, " +
           "{$in: [{$dayOfWeek: '$scheduledDateTime'}, ?3]}" +
           "]}}")
    List<TrialSession> findByMentorIdAndTimePattern(Long mentorId, Integer startHour, Integer endHour, List<Integer> daysOfWeek);
    
    // Find sessions that need confirmation
    List<TrialSession> findByMentorIdAndRequireConfirmationTrueAndStatus(Long mentorId, TrialSessionStatus status);
    
    // Find sessions with specific time zone
    List<TrialSession> findByMentorIdAndTimeZone(Long mentorId, String timeZone);
    
    // Find sessions by multiple IDs with mentor validation (for bulk operations)
    @Query("{'id': {$in: ?0}, 'mentorId': ?1}")
    List<TrialSession> findByIdInAndMentorId(List<Long> sessionIds, Long mentorId);
    
    // Count available sessions for a mentor in date range
    @Query(value = "{'mentorId': ?0, 'status': 'AVAILABLE', 'scheduledDateTime': {$gte: ?1, $lte: ?2}}", count = true)
    Long countAvailableSessionsByMentorInDateRange(Long mentorId, LocalDateTime startDate, LocalDateTime endDate);
}
