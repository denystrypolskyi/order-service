package com.example.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.order_service.model.Order;

public record OrderResponseDTO(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items) {

    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemResponseDTO::from)
                        .toList());
    }
}
