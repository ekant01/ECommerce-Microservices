package com.janapure.microservices.product_service.grpc;


import com.janapure.microservices.product_service.model.Inventory;
import com.janapure.microservices.product_service.model.Product;
import com.janapure.microservices.product_service.repositories.InventoryRepo;
import com.janapure.microservices.product_service.repositories.ProductRepo;
import com.janapure.microservices.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
public class ProductgRPCImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private ProductRepo productRepo;

    private InventoryRepo inventoryRepo;

    public ProductgRPCImpl(ProductRepo productRepo, InventoryRepo inventoryRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @Override
    public void checkStock(CheckStockRequest request, StreamObserver<CheckStockResponse> responseObserver) {
        String productId = request.getProductId();
        int quantity = request.getQuantity();
        Product product = productRepo.findByPdId(productId);
        if (product != null) {
            Inventory inventory = inventoryRepo.findByProductId(product.getPdId());
            int availableStock = (inventory != null) ? inventory.getQuantityAvailable() : 0;
            CheckStockResponse response = CheckStockResponse.newBuilder()
                    .setAvailableQuantity(availableStock)
                    .setIsAvailable(availableStock >= quantity)
                    .build();
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Product not found"));
        }
        responseObserver.onCompleted();

    }

    @Override
    public void getProductInfo(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {

        String productId = request.getProductId();
        System.out.println("Product request: " + productId);
        Product product = productRepo.findByPdId(productId);
        if (product != null) {
            ProductResponse response = ProductResponse.newBuilder()
                    .setProductId(product.getPdId())
                    .setName(product.getProductName())
                    .setPrice(product.getPrice())
                    .setDescription(product.getDescription())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            System.out.println("Product not found: " + productId);
            //responseObserver.onError(new RuntimeException("Product not found"));
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Product not found: " + productId).asRuntimeException()
            );
        }

    }

    @Override
    public void reserveStock(StockUpdateRequest request, StreamObserver<StockUpdateResponse> responseObserver) {
        try {
            String productId = request.getProductId();
            int quantity = request.getQuantity();

            // Fetch product and inventory
            Product product = productRepo.findByPdId(productId);
            if (product == null) {
                throw new RuntimeException("Product not found: " + productId);
            }

            Inventory inventory = inventoryRepo.findByProductId(product.getPdId());
            if (inventory == null) {
                throw new RuntimeException("Inventory not found for product: " + productId);
            }

            synchronized (this) { // Prevent race conditions
                if (inventory.getQuantityAvailable() < quantity) {
                    throw new RuntimeException("Insufficient stock for product: " + productId);
                }

                // Reserve stock
                inventory.reserveStock(quantity);
                inventoryRepo.save(inventory);
            }

            // Send success response
            StockUpdateResponse response = StockUpdateResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Stock reserved successfully")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

}
