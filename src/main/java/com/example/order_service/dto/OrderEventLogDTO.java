package com.example.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.order_service.enums.OrderEventType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderEventLogDTO {

    @NotNull(message = "Event type must not be null")
    private OrderEventType event;

    @NotNull(message = "Order ID must not be null")
    private Long orderId;

    @NotNull(message = "Total amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotNull(message = "CreatedAt must not be null")
    private LocalDateTime createdAt;
}
