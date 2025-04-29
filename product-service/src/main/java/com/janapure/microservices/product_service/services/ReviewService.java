package com.janapure.microservices.product_service.services;

import com.janapure.microservices.product_service.dto.ReviewDTO;
import com.janapure.microservices.product_service.model.Review;
import com.janapure.microservices.product_service.repositories.ReviewRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    public ReviewDTO createReview(ReviewDTO review, String productId) {


        Review reviewEntity = new Review();
        reviewEntity.setProductId(productId);
        reviewEntity.setRating(review.getRating());
        reviewEntity.setComment(review.getComment());
        reviewEntity.setCreatedAt(System.currentTimeMillis());
        reviewEntity.setModifiedAt(System.currentTimeMillis());
        reviewRepo.save(reviewEntity);

        review.setId(reviewEntity.getId());
        review.setCreatedAt(reviewEntity.getCreatedAt());
        review.setModifiedAt(reviewEntity.getModifiedAt());
        review.setUserId(reviewEntity.getUserId());
        review.setProductId(reviewEntity.getProductId());

        return review;
    }

    public ReviewDTO updateReview(ReviewDTO review, String productId) {

        Review reviewEntity = reviewRepo.findById(review.getId()).orElse(null);
        if (reviewEntity != null) {
//            if (userDetail.getUserId()!=null && !userDetail.getUserId().equals(reviewEntity.getUserId())) {
//                return null; // User is not authorized to update this review
//            }
            reviewEntity.setProductId(productId);
            //reviewEntity.setUserId(userDetail.getUserId());
            reviewEntity.setRating(review.getRating());
            reviewEntity.setComment(review.getComment());
            reviewEntity.setModifiedAt(System.currentTimeMillis());
            reviewRepo.save(reviewEntity);

            review.setCreatedAt(reviewEntity.getCreatedAt());
            review.setModifiedAt(reviewEntity.getModifiedAt());
            review.setUserId(reviewEntity.getUserId());
            review.setProductId(reviewEntity.getProductId());

            return review;
        }
        return null; // Review not found
    }



    public void deleteReview(String reviewId) {
//        EUserDetail userDetail = (EUserDetail) SecurityContextHolder
//                .getContext().getAuthentication().getPrincipal();
        Review reviewEntity = reviewRepo.findByIdAndIsDeletedFalse(reviewId);
        if (reviewEntity != null) {
//            if (userDetail.getUserId()!=null && !userDetail.getUserId().equals(reviewEntity.getUserId())) {
//                return; // User is not authorized to delete this review
//            }
            reviewEntity.setDeleted(false);
            reviewRepo.save(reviewEntity);
        }

    }

    public Page<ReviewDTO> getReviews(String productId, int page, int size) {

        Page<Review> reviews = reviewRepo.findByProductIdAndDeleted(productId, false, PageRequest.of(page, size));
        return reviews.map(review -> {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(review.getId());
            reviewDTO.setProductId(review.getProductId());
            reviewDTO.setUserId(review.getUserId());
            reviewDTO.setRating(review.getRating());
            reviewDTO.setComment(review.getComment());
            reviewDTO.setCreatedAt(review.getCreatedAt());
            reviewDTO.setModifiedAt(review.getModifiedAt());
            return reviewDTO;
        });
    }
}
