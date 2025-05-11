package com.janapure.microservices.order_service.service;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janapure.microservices.order_service.dto.OrderCreatePayload;
import com.janapure.microservices.order_service.enities.Order;
import com.janapure.microservices.order_service.enities.OrderItem;
import com.janapure.microservices.order_service.repo.OrderItemRepo;
import com.janapure.microservices.order_service.repo.OrderRepo;
import com.janapure.microservices.proto.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepo orderRepo;

    private OrderItemRepo orderItemRepo;

    private ObjectMapper objectMapper;

    private ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;

    public OrderService() {
    }

    public OrderService(OrderRepo orderRepo, OrderItemRepo orderItemRepo, ObjectMapper objectMapper,
                        ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.objectMapper = objectMapper;
        this.productServiceBlockingStub = productServiceBlockingStub;
    }

    @KafkaListener(topics = "order.create.request", groupId = "order-service")
    public void order(String eventJson) {
        System.out.println("Raw event received: " + eventJson);

        try {

            OrderCreatePayload payload = objectMapper.readValue(eventJson , OrderCreatePayload.class);
            System.out.println("Parsed payload: " + payload);

            // Check for stock availability
            for (OrderCreatePayload.OrderItem item : payload.getItems()) {
                // Assuming productServiceBlockingStub has a method to check stock
                CheckStockRequest checkStockRequest = CheckStockRequest.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build();
                CheckStockResponse isInStock = productServiceBlockingStub.checkStock(checkStockRequest);
                if (!isInStock.getIsAvailable()) {
                    throw new RuntimeException("Product " + item.getProductId() + " is out of stock.");
                }
                // Reserve stock
                StockUpdateRequest stockUpdateRequest = StockUpdateRequest.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build();
                 StockUpdateResponse stockUpdateResponse = productServiceBlockingStub.reserveStock(stockUpdateRequest);
                if (!stockUpdateResponse.getSuccess()) {
                    throw new RuntimeException("Failed to reserve stock for product " + item.getProductId());
                }
            }
            // proceed to create order
            Order order = new Order();
            order.setUserId(payload.getUserId());
            order.setOrderId(UUID.randomUUID().toString());
            order.setTotalAmount(calculateTotalAmount(payload));
            order.setOrderStatus("PAYMENT_PENDING");
            order.setPaymentMode(payload.getPaymentMode());
            order.setOrderDate(LocalDateTime.now());
            order.setOrderItems(payload.getItems().stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(item.getProductId());
                orderItem.setProductName(item.getProductName());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                return orderItem;
            }).collect(Collectors.toList()));

            orderRepo.save(order);

        } catch (JsonProcessingException e) {
            System.out.println("Parsed payload: " + e);
            //e.printStackTrace();
            throw new RuntimeException("Failed to parse event JSON", e);
        }
    }

    private double calculateTotalAmount(OrderCreatePayload payload) {
        double totalAmount = 0.0;
        for (OrderCreatePayload.OrderItem item : payload.getItems()) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        return totalAmount;
    }

}
