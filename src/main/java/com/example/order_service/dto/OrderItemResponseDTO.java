package com.example.order_service.dto;

import java.math.BigDecimal;

import com.example.order_service.model.OrderItem;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        String description,
        int quantity,
        BigDecimal pricePerUnit) {

    public static OrderItemResponseDTO from(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getDescription(),
                item.getQuantity(),
                item.getPricePerUnit());
    }
}
