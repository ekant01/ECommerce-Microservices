package com.janapure.microservices.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;

    private String productName;

    private String pdId;

    private String description;

    private String category;

    private String sellerId;

    private double price;

    private boolean isDeleted;

    private Map<String,Object> attributes;

    private String imageUrl;

    private Long createdAt;

    private Long modifiedAt;

}
