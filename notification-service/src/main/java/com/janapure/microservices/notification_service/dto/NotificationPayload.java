package com.janapure.microservices.notification_service.dto;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload {
    private String userId;
    private String orderId;
    private String message;
}
