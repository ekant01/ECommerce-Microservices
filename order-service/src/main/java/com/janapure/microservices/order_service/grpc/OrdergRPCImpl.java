package com.janapure.microservices.order_service.grpc;

import com.janapure.microservices.order_service.enities.Order;
import com.janapure.microservices.order_service.enities.OrderItem;
import com.janapure.microservices.order_service.repo.OrderRepo;
import com.janapure.microservices.proto.OrderServiceGrpc;
import com.janapure.microservices.proto.ProductListRequest;
import com.janapure.microservices.proto.ProductListResponse;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
public class OrdergRPCImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Autowired
    private OrderRepo orderRepo;

    @Transactional
    @Override
    public void getProductList(ProductListRequest request, StreamObserver<ProductListResponse> responseObserver) {
        // Implementation for getting product list
        // This is just a placeholder, actual implementation will depend on your business logic

        String orderId = request.getOrderId();
        Order order = orderRepo.findByOrderId(orderId);
        if (order == null) {
            return;
        }
        Map<String, Integer> productIds = order.getOrderItems().stream()
                .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));

        ProductListResponse response = ProductListResponse.newBuilder()
                .putAllProductIds(productIds) // Add the list of product IDs to the response
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
