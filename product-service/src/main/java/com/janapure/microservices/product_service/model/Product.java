package com.janapure.microservices.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ec_product")
public class Product {

    @Id
    private String id;

    private String productName;

    private String pdId;

    private String description;

    private String category;

    private String sellerId;

    private double price;

    private boolean isDeleted;

    private String imageUrl;

    private Map<String,Object> attributes;

    private Long createdAt;

    private Long modifiedAt;
}
