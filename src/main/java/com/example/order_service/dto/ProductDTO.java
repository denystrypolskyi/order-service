package com.example.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {

    @NotNull(message = "Product ID must not be null")
    private Long id;

    @NotBlank(message = "Product name must not be blank")
    private String name;

    @NotBlank(message = "Product description must not be blank")
    private String description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Quantity must be 0 or more")
    private int quantity;
}
