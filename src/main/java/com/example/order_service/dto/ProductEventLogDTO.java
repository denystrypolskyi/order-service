package com.example.order_service.dto;

import com.example.order_service.enums.ProductEventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEventLogDTO {

    @NotNull(message = "Event type must not be null")
    private ProductEventType event;

    @NotNull(message = "Product ID must not be null")
    private Long productId;

    @NotNull(message = "CreatedAt must not be null")
    private LocalDateTime createdAt;
}
