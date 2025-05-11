package com.janapure.microservices.cart_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private Long id;

    private String userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CartItemDto> cartItems;
}
