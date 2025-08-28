package com.stemlen.service;

import com.stemlen.dto.PaymentOrderDTO;
import com.stemlen.exception.PortalException;

public interface PaymentService {
    PaymentOrderDTO createOrder(Long subscriptionId, Long amountPaise, String currency) throws PortalException;
    PaymentOrderDTO markPaid(String gatewayOrderId) throws PortalException;
}
