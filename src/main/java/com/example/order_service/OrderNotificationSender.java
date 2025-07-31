package com.example.order_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.order_service.dto.OrderNotificationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderNotificationSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
 
    public void sendOrderNotification(@Valid OrderNotificationDTO notification) {
        try {
            String json = objectMapper.writeValueAsString(notification);
            rabbitTemplate.convertAndSend("order.notifications", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); 
        }
    }
}
