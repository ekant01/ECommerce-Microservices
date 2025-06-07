package com.janapure.microservices.order_service.grpc;

import com.janapure.microservices.proto.OrderServiceGrpc;
import com.janapure.microservices.proto.ProductListRequest;
import com.janapure.microservices.proto.ProductListResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class OrdergRPCImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Override
    public void getProductList(ProductListRequest request, StreamObserver<ProductListResponse> responseObserver) {
        // Implementation for getting product list
        // This is just a placeholder, actual implementation will depend on your business logic
        List<String> productIds = List.of("prod1", "prod2", "prod3"); // Example product IDs
        ProductListResponse response = ProductListResponse.newBuilder()
                .addAllProductIds(productIds)  // Add the list of product IDs to the response
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
