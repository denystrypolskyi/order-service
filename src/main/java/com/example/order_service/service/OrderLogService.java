package com.example.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.order_service.dto.OrderEventLogDTO;
import com.example.order_service.dto.ProductEventLogDTO;
import com.example.order_service.enums.OrderEventType;
import com.example.order_service.enums.ProductEventType;
import com.example.order_service.kafka.KafkaLoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderLogService {

    private final KafkaLoggerService kafkaLoggerService;
    private final ObjectMapper objectMapper;

    public void logProductNotFound(Long productId) {
        try {
            ProductEventLogDTO logEntry = new ProductEventLogDTO(
                    ProductEventType.PRODUCT_NOT_FOUND,
                    productId,
                    LocalDateTime.now());
            String jsonLog = objectMapper.writeValueAsString(logEntry);
            kafkaLoggerService.logProduct(jsonLog);
        } catch (Exception e) {
            System.err.println("Logging error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logOrderRejected(BigDecimal amount) {
        try {
            OrderEventLogDTO logEntry = new OrderEventLogDTO(
                    OrderEventType.ORDER_REJECTED,
                    null,
                    amount,
                    LocalDateTime.now());
            String jsonLog = objectMapper.writeValueAsString(logEntry);
            kafkaLoggerService.logOrder(jsonLog);
        } catch (Exception e) {
            System.err.println("Logging error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logOrderCreated(Long orderId, BigDecimal amount, LocalDateTime createdAt) {
        try {
            OrderEventLogDTO logEntry = new OrderEventLogDTO(
                    OrderEventType.ORDER_CREATED,
                    orderId,
                    amount,
                    createdAt);
            String jsonLog = objectMapper.writeValueAsString(logEntry);
            kafkaLoggerService.logOrder(jsonLog);
        } catch (Exception e) {
            System.err.println("Logging error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
