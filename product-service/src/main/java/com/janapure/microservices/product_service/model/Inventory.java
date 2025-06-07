package com.janapure.microservices.product_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ec_inventory")
public class Inventory {

    @Id
    private String id;

    private String productId;

    private String warehouseId;

    private int quantityAvailable;

    private int reservedQuantity;

    private String status; // Consider using an enum for status

    private Long createdAt;

    private Long updatedAt;

    private String createdBy;

    private String updatedBy;

    public void reserveStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reserve must be positive.");
        }
        if (quantity > quantityAvailable) {
            throw new RuntimeException("Not enough inventory available. Available: " + quantityAvailable);
        }
        this.quantityAvailable -= quantity;
        this.reservedQuantity += quantity;
    }

    public void releaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to release must be positive.");
        }
        if (quantity > reservedQuantity) {
            throw new RuntimeException("Not enough inventory reserved. Reserved: " + reservedQuantity);
        }
        this.reservedQuantity -= quantity;
        this.quantityAvailable += quantity;
    }

    // Called when order is successfully completed
    public void confirmOrder(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to confirm must be positive.");
        }
        if (quantity > reservedQuantity) {
            throw new RuntimeException("Cannot confirm more than reserved quantity. Reserved: " + reservedQuantity);
        }
        this.reservedQuantity -= quantity;
        // quantityAvailable already reduced at reservation time
    }
}
