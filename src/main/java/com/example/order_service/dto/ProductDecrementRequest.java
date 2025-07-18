package com.example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDecrementRequest {
    private Long productId;
    private int quantity;
}
