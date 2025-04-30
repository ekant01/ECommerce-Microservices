package com.janapure.microservices.product_service.repositories;

import com.janapure.microservices.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends MongoRepository<Product, String>, PagingAndSortingRepository<Product,String> {

    Product findByPdid(String productId);

}
