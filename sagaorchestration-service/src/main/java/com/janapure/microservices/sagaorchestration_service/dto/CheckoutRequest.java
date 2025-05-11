package com.janapure.microservices.sagaorchestration_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private String userId;
    private String paymentMode;
    private List<OrderItem> items;

    @Data
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
    }
}
