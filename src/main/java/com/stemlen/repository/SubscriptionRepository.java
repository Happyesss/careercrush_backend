package com.stemlen.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.stemlen.entity.Subscription;
import com.stemlen.dto.SubscriptionStatus;

public interface SubscriptionRepository extends MongoRepository<Subscription, Long> {
    List<Subscription> findByMenteeId(Long menteeId);
    List<Subscription> findByMentorId(Long mentorId);
    List<Subscription> findByStatus(SubscriptionStatus status);
}
