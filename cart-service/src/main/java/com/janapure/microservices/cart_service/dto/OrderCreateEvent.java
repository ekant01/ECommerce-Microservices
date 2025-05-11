package com.janapure.microservices.cart_service.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderCreateEvent {
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

