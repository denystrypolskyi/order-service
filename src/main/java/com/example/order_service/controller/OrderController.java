package com.example.order_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.dto.OrderCreateDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.security.UserContext;
import com.example.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllOrders().stream()
                .map(OrderResponseDTO::from)
                .toList());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        service.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        Long userId = UserContext.getUserId();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(service.getOrdersByUserId(userId).stream()
                .map(OrderResponseDTO::from)
                .toList());
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        String email = UserContext.getEmail();

        if (userId == null || email == null || email.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(OrderResponseDTO.from(service.createOrder(dto, userId, email)));
    }
}
