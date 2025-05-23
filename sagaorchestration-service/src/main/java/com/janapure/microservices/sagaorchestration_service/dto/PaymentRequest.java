package com.janapure.microservices.sagaorchestration_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String orderId;
    private String userId;
    private double amount;
    private String paymentMode;
}

