package com.janapure.microservices.cart_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCartItemRequest {

    private String userId;

    private String productId;

    private String productName;

    private int quantity;

    private double price;
}
