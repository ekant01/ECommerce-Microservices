package com.janapure.microservices.order_service.enities;


import com.janapure.microservices.order_service.dto.OrderCreatePayload;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ec_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "order_id", unique = true)
    private String orderId;

    private String userId;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_items")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

}
