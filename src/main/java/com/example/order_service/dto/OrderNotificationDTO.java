package com.example.order_service.dto;

import com.example.order_service.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {
    private String email;
    private Order order;
}
