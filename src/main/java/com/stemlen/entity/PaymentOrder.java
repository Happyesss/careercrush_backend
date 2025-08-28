package com.stemlen.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.PaymentOrderDTO;
import com.stemlen.dto.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payment_orders")
public class PaymentOrder {
    @Id
    private Long id;
    private Long subscriptionId;
    private String gatewayOrderId; // Razorpay order id
    private String currency; // INR
    private Long amountPaise; // use paise
    private PaymentStatus status; // CREATED, PAID, FAILED
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentOrderDTO toDTO(){
        PaymentOrderDTO d = new PaymentOrderDTO();
        d.setId(id);
        d.setSubscriptionId(subscriptionId);
        d.setGatewayOrderId(gatewayOrderId);
        d.setCurrency(currency);
        d.setAmountPaise(amountPaise);
        d.setStatus(status);
        d.setNotes(notes);
        d.setCreatedAt(createdAt);
        d.setUpdatedAt(updatedAt);
        return d;
    }
}
