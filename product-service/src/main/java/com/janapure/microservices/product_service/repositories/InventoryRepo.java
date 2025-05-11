package com.janapure.microservices.product_service.repositories;

import com.janapure.microservices.product_service.model.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepo extends MongoRepository<Inventory, String> {

    Inventory findByProductId(String productId);


}
