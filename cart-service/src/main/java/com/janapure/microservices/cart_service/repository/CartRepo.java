package com.janapure.microservices.cart_service.repository;


import com.janapure.microservices.cart_service.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(String userId);
}
