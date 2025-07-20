package com.example.order_service.controller;

import com.example.order_service.dto.OrderCreateDTO;
import com.example.order_service.jwt.JwtUtil;
import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return ResponseEntity.ok(service.createOrder(dto, email));
    }
}
