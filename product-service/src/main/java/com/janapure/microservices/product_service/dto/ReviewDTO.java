package com.janapure.microservices.product_service.dto;


import lombok.Data;

@Data
public class ReviewDTO {

    private String id;

    private String productId;

    private String userId;

    private Double rating;

    private String comment;

    private Long createdAt;

    private Long modifiedAt;

}
