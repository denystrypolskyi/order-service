package com.example.order_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.order_service.dto.OrderNotificationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderNotificationSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OrderNotificationSender(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrderNotification(OrderNotificationDTO notification) {
        try {
            String json = objectMapper.writeValueAsString(notification);
            rabbitTemplate.convertAndSend("order.notifications", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); 
        }
    }
}
