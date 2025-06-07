package com.janapure.microservices.product_service.dto;

import lombok.Data;

@Data
public class InventoryDTO {

    private String productId;

    private int quantityAvailable;
}
