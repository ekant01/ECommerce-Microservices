package com.janapure.microservices.cart_service.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.janapure.microservices.proto.UserServiceGrpc;
import com.janapure.microservices.proto.ProductServiceGrpc;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcClientConfig {

    private ManagedChannel userServiceChannel;
    private ManagedChannel productServiceChannel;

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub() {
        userServiceChannel = ManagedChannelBuilder.forAddress("localhost", 9191)
                .usePlaintext()
                .build();
        return UserServiceGrpc.newBlockingStub(userServiceChannel);
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        productServiceChannel = ManagedChannelBuilder.forAddress("localhost", 9192)
                .usePlaintext()
                .build();
        return ProductServiceGrpc.newBlockingStub(productServiceChannel);
    }

    @PreDestroy
    public void shutdownChannels() {
        if (userServiceChannel != null) {
            userServiceChannel.shutdown();
        }
        if (productServiceChannel != null) {
            productServiceChannel.shutdown();
        }
    }
}