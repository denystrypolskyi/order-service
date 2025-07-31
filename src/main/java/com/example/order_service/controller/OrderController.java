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
import com.example.order_service.model.Order;
import com.example.order_service.security.Authenticated;
import com.example.order_service.security.UserContext;
import com.example.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @Authenticated
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @Authenticated
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        service.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @Authenticated
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders() {
        Long userId = UserContext.getUserId();

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Order> orders = service.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        String email = UserContext.getEmail();

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(service.createOrder(dto, email));
    }
}