package com.stemlen.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.stemlen.dto.TrialSessionStatus;
import com.stemlen.entity.TrialSession;

@Repository
public interface TrialSessionRepository extends MongoRepository<TrialSession, Long> {
    
    // Find trial sessions by mentor ID
    List<TrialSession> findByMentorId(Long mentorId);
    
    // Find trial sessions by mentee ID
    List<TrialSession> findByMenteeId(Long menteeId);
    
    // Find trial sessions by status
    List<TrialSession> findByStatus(TrialSessionStatus status);
    
    // Find available trial sessions for a mentor
    List<TrialSession> findByMentorIdAndStatus(Long mentorId, TrialSessionStatus status);
    
    // Find trial sessions by package ID
    List<TrialSession> findByPackageId(Long packageId);
    
    // Find trial sessions scheduled after a specific date
    @Query("{ 'scheduledDateTime' : { $gte: ?0 } }")
    List<TrialSession> findByScheduledDateTimeAfter(LocalDateTime dateTime);
    
    // Find trial sessions for a mentor within a date range
    @Query("{ 'mentorId' : ?0, 'scheduledDateTime' : { $gte: ?1, $lte: ?2 } }")
    List<TrialSession> findByMentorIdAndScheduledDateTimeBetween(Long mentorId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find available trial sessions for a specific date
    @Query("{ 'status' : 'AVAILABLE', 'scheduledDateTime' : { $gte: ?0, $lt: ?1 } }")
    List<TrialSession> findAvailableSessionsForDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // Find booked trial sessions by mentee email
    List<TrialSession> findByMenteeEmailAndStatus(String menteeEmail, TrialSessionStatus status);
}