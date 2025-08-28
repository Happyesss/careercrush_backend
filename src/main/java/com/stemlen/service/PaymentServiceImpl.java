package com.stemlen.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.PaymentOrderDTO;
import com.stemlen.dto.PaymentStatus;
import com.stemlen.dto.SubscriptionStatus;
import com.stemlen.entity.PaymentOrder;
import com.stemlen.entity.Subscription;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.PaymentOrderRepository;
import com.stemlen.repository.SubscriptionRepository;
import com.stemlen.utility.Utilities;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentOrderRepository orderRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public PaymentOrderDTO createOrder(Long subscriptionId, Long amountPaise, String currency) throws PortalException {
    subscriptionRepository.findById(subscriptionId)
        .orElseThrow(() -> new PortalException("SUBSCRIPTION_NOT_FOUND"));

        PaymentOrder order = new PaymentOrder();
        order.setId(Utilities.getNextSequence("paymentOrders"));
        order.setSubscriptionId(subscriptionId);
        // In a real integration, call Razorpay to create an order and store the gateway id
        order.setGatewayOrderId("local_" + order.getId());
        order.setCurrency(currency != null ? currency : "INR");
        order.setAmountPaise(amountPaise);
        order.setStatus(PaymentStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order).toDTO();
    }

    @Override
    public PaymentOrderDTO markPaid(String gatewayOrderId) throws PortalException {
        PaymentOrder order = orderRepository.findByGatewayOrderId(gatewayOrderId)
                .orElseThrow(() -> new PortalException("ORDER_NOT_FOUND"));
        order.setStatus(PaymentStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Activate subscription
        Subscription sub = subscriptionRepository.findById(order.getSubscriptionId())
                .orElseThrow(() -> new PortalException("SUBSCRIPTION_NOT_FOUND"));
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(sub);

        return order.toDTO();
    }
}
