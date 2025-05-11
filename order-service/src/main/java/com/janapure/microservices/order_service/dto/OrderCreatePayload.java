package com.janapure.microservices.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatePayload {
    private String userId;
    private List<OrderItem> items;
    private String paymentMode;

    @Data
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
    }
}

