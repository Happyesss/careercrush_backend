package com.stemlen.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.PaymentOrderDTO;
import com.stemlen.dto.ResponseDTO;
import com.stemlen.exception.PortalException;
import com.stemlen.service.PaymentService;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/payments")
public class PaymentAPI {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderDTO> createOrder(
            @RequestParam Long subscriptionId,
            @RequestParam Long amountPaise,
            @RequestParam(defaultValue = "INR") String currency) throws PortalException {
        return new ResponseEntity<>(paymentService.createOrder(subscriptionId, amountPaise, currency), HttpStatus.CREATED);
    }

    // Webhook placeholder to mark order paid
    @PostMapping("/webhook/paid")
    public ResponseEntity<ResponseDTO> markPaid(@RequestParam String gatewayOrderId) throws PortalException {
        paymentService.markPaid(gatewayOrderId);
        return new ResponseEntity<>(new ResponseDTO("Order marked as PAID and subscription activated"), HttpStatus.OK);
    }
}
