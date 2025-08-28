package com.stemlen.dto;

import java.time.LocalDateTime;

import com.stemlen.entity.PaymentOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderDTO {
    private Long id;
    private Long subscriptionId;
    private String gatewayOrderId;
    private String currency;
    private Long amountPaise;
    private PaymentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentOrder toEntity(){
        PaymentOrder p = new PaymentOrder();
        p.setId(id);
        p.setSubscriptionId(subscriptionId);
        p.setGatewayOrderId(gatewayOrderId);
        p.setCurrency(currency);
        p.setAmountPaise(amountPaise);
        p.setStatus(status);
        p.setNotes(notes);
        p.setCreatedAt(createdAt);
        p.setUpdatedAt(updatedAt);
        return p;
    }
}
