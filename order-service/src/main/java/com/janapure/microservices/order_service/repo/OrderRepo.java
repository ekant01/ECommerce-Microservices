package com.janapure.microservices.order_service.repo;

import com.janapure.microservices.order_service.enities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {

}
