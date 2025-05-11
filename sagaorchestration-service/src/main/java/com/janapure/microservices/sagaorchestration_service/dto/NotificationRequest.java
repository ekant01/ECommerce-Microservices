package com.janapure.microservices.sagaorchestration_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String userId;
    private String orderId;
    private String message;
}

