package com.janapure.microservices.payment_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janapure.microservices.payment_service.events.PaymentFailedEvent;
import com.janapure.microservices.payment_service.events.PaymentRequest;
import com.janapure.microservices.payment_service.events.PaymentSuccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment.initiate", groupId = "payment-group")
    public void payment(String paymentRequest) throws JsonProcessingException {

        System.out.println("Payment processed successfully."+ paymentRequest);

        PaymentRequest request = objectMapper.readValue(paymentRequest, PaymentRequest.class);
        /*
          Simulating payment processing actual logic here.
          In a real-world application, you would integrate with a payment gateway or service.
          For demonstration purposes, we will assume that the payment is successful if the payment mode is "CARD".
         */
        if (request.getPaymentMode().equals("CARD")){
            kafkaTemplate.send("payment.success", new PaymentSuccessEvent(request.getOrderId(), request.getUserId()));
        }else {
            kafkaTemplate.send("payment.failed", new PaymentFailedEvent(request.getOrderId(), request.getUserId(), "Payment failed due to insufficient balance."));
        }

    }
}
