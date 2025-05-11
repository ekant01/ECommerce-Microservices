package com.janapure.microservices.sagaorchestration_service.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janapure.microservices.sagaorchestration_service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestrator.class);

    private static final String CHECKOUT_REQUEST_TOPIC = "checkout.request";
    private static final String ORDER_CREATE_REQUEST_TOPIC = "order.create.request";
    private static final String PAYMENT_INITIATE_TOPIC = "payment.initiate";
    private static final String NOTIFICATION_SEND_TOPIC = "notification.send";
    private static final String ORDER_UPDATE_TOPIC = "order.update";
    private static final String CART_CLEAR_TOPIC = "cart.clear";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SagaOrchestrator(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = CHECKOUT_REQUEST_TOPIC)
    public void handleCheckoutRequest(String request) {
        processEvent(request, CheckoutRequest.class, checkoutRequest ->
                kafkaTemplate.send(ORDER_CREATE_REQUEST_TOPIC, checkoutRequest)
        );
    }

    @KafkaListener(topics = "order.created")
    public void handleOrderCreated(String event) {
        processEvent(event, OrderCreatedEvent.class, orderCreated -> {
            PaymentRequest paymentRequest = new PaymentRequest(
                    orderCreated.getOrderId(), orderCreated.getUserId(),
                    orderCreated.getTotalAmount(), orderCreated.getPaymentMode()
            );
            kafkaTemplate.send(PAYMENT_INITIATE_TOPIC, paymentRequest);
        });
    }

    @KafkaListener(topics = "payment.success")
    public void handlePaymentSuccess(String event) {
        processEvent(event, PaymentSuccessEvent.class, paymentSuccess -> {
            kafkaTemplate.send(NOTIFICATION_SEND_TOPIC, new NotificationRequest(
                    paymentSuccess.getUserId(), paymentSuccess.getOrderId(), "Your payment was successful."
            ));
            kafkaTemplate.send(ORDER_UPDATE_TOPIC, new OrderStatusUpdate(paymentSuccess.getOrderId(), "COMPLETED"));
            kafkaTemplate.send(CART_CLEAR_TOPIC, new CartClearEvent(paymentSuccess.getUserId()));
        });
    }

    @KafkaListener(topics = "payment.failed")
    public void handlePaymentFailure(String event) {
        processEvent(event, PaymentFailedEvent.class, paymentFailed ->
                kafkaTemplate.send(ORDER_UPDATE_TOPIC, new OrderStatusUpdate(paymentFailed.getOrderId(), "CANCELLED"))
        );
    }

    private <T> void processEvent(String event, Class<T> eventType, java.util.function.Consumer<T> handler) {
        try {
            T parsedEvent = objectMapper.readValue(event, eventType);
            handler.accept(parsedEvent);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse event: {}", event, e);
            throw new RuntimeException("Failed to parse event", e);
        }
    }
}