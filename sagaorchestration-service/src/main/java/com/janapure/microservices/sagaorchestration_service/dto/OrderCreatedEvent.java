package com.janapure.microservices.sagaorchestration_service.dto;


import lombok.Data;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private double totalAmount;
    private String paymentMode;
}
