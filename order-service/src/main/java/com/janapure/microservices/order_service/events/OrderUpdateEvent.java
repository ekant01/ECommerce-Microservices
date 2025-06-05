package com.janapure.microservices.order_service.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateEvent {
    private String orderId;
    private String userId;
    private String status; // e.g., "CREATED", "PAYMENT_PENDING", "PAYMENT_SUCCESS", "PAYMENT_FAILED"
}
