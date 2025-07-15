package com.example.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Total amount is required")
    @Min(value = 0, message = "Total amount must be non-negative")
    private Long totalAmount;

    
}
