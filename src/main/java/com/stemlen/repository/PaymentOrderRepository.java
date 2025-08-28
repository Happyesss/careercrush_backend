package com.stemlen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.stemlen.entity.PaymentOrder;
import com.stemlen.dto.PaymentStatus;

public interface PaymentOrderRepository extends MongoRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByGatewayOrderId(String gatewayOrderId);
    List<PaymentOrder> findBySubscriptionId(Long subscriptionId);
    List<PaymentOrder> findByStatus(PaymentStatus status);
}
