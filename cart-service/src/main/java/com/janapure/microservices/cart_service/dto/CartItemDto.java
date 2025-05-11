package com.janapure.microservices.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;

    private String productId;

    private String productName;

    private int quantity;

    private double price;

    private LocalDateTime addedAt;

}
