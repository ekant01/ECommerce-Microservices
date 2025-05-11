package com.janapure.microservices.cart_service.dto;


import lombok.Data;

@Data
public class CheckOutRequest {

    String userId;

    String paymentMode;
}
