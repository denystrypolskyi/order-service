package com.example.order_service.service;

import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(OrderRequestDTO request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(request.getStatus());
        order.setTotalAmount(request.getTotalAmount());
        order.setCreatedAt(LocalDateTime.now());

        return repository.save(order);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
