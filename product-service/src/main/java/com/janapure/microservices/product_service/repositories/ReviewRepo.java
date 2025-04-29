package com.janapure.microservices.product_service.repositories;

import com.janapure.microservices.product_service.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepo extends MongoRepository<Review,String>, PagingAndSortingRepository<Review,String> {

    Review findByIdAndIsDeletedFalse(String reviewId);

    Page<Review> findByProductIdAndDeleted(String productId, boolean deleted, Pageable pageable);

}
