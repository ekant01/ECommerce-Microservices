package com.janapure.microservices.product_service.controller;


import com.janapure.microservices.product_service.dto.ReviewDTO;
import com.janapure.microservices.product_service.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public class ReviewController {
    // Add methods to handle review-related requests here
    // For example, you can add methods to create, update, delete, and retrieve reviews

    @Autowired
    private ReviewService reviewService;

    @PostMapping("products/{productId}/reviews")
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody ReviewDTO review,
            @PathVariable String productId
    ) {
        // Implement logic to save the review

        reviewService.createReview(review, productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review, productId));
    }


    @RequestMapping(path = "products/{productId}/reviews", method = PUT)
    public ResponseEntity<ReviewDTO> updateReview(
            @RequestBody ReviewDTO review,
            @PathVariable String productId
    ) {
        // Implement logic to save the review

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.updateReview(review, productId));
    }

    @RequestMapping(path = "products/reviews/{reviewId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteReview(
            @PathVariable String reviewId
    ) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "products/{productId}/reviews", method = RequestMethod.GET)
    public ResponseEntity<Page<ReviewDTO>> getReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewDTO> reviews = reviewService.getReviews(productId, page, size);
        return ResponseEntity.ok(reviews);
    }
}
