package com.janapure.microservices.order_service.repo;

import com.janapure.microservices.order_service.enities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
