package com.janapure.microservices.payment_service.events;


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
