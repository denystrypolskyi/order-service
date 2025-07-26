package com.example.order_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaLoggerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void logProduct(String message) {
        kafkaTemplate.send("product-logs", message);
    }

    public void logOrder(String message) {
        kafkaTemplate.send("order-logs", message);
    }
}
