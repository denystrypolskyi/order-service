package com.example.order_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public OrderNotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderNotification(String message) {
        rabbitTemplate.convertAndSend("order-exchange", "order.notifications.created", message);
    }
}

