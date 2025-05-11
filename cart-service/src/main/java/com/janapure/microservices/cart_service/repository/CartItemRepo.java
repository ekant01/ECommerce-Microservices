package com.janapure.microservices.cart_service.repository;

import com.janapure.microservices.cart_service.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

}
