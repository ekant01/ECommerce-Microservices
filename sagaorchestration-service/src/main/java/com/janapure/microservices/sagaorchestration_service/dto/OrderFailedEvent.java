package com.janapure.microservices.sagaorchestration_service.dto;


import lombok.Data;

@Data
public class OrderFailedEvent {

    String userId;

    String msg;
}
