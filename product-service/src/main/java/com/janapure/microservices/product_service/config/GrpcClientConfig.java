package com.janapure.microservices.product_service.config;

import com.janapure.microservices.proto.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public io.grpc.ManagedChannel userServiceChannel() {
        return io.grpc.ManagedChannelBuilder.forAddress("localhost", 9193)
                .usePlaintext()
                .build();
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub(ManagedChannel orderServiceChannel) {
        return OrderServiceGrpc.newBlockingStub(orderServiceChannel);
    }

}
