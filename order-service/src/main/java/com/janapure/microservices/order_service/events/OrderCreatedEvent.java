package com.janapure.microservices.order_service.events;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private double totalAmount;
    private String paymentMode;
}
