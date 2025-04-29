package com.janapure.microservices.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ec_product_review")
public class Review {

    @Id
    private String id;

    private String productId;

    private String userId;

    private Double rating;

    private String comment;

    private Long createdAt;

    private Long modifiedAt;

    private boolean isDeleted;

}
