package com.janapure.microservices.notification_service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janapure.microservices.notification_service.dto.NotificationPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    public MailService(JavaMailSender javaMailSender, ObjectMapper objectMapper) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = objectMapper;
    }

    public MailService() {

    }

    // Method to send email
    @KafkaListener(topics = "notification.send", groupId = "notification-service")
    public void sendEmail(String event) {

        try {
            NotificationPayload notificationPayload = objectMapper.readValue(event, NotificationPayload.class);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("ec-janapure@gmail.com");
            mailMessage.setTo(notificationPayload.getUserId());
            mailMessage.setSubject("Order Confirmation");
            mailMessage.setText("Your order with ID " + notificationPayload.getOrderId() + " has been successfully placed.");
            javaMailSender.send(mailMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
