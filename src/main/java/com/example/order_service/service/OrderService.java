package com.example.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.order_service.OrderNotificationSender;
import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderCreateDTO;
import com.example.order_service.dto.OrderNotificationDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.dto.ProductDecrementRequest;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final OrderNotificationSender orderNotificationSender;
    private final OrderLogService orderLogService;

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new Error("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new Error("You are not allowed to delete this order");
        }
        repository.delete(order);
    }

    public Order mapToOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());

        List<OrderItem> items = dto.getItems().stream().map(itemDTO -> {
            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            item.setOrder(order);
            return item;
        }).toList();

        order.setItems(items);
        return order;
    }

    @Transactional
    public Order createOrder(OrderCreateDTO dto, String email) {
        Order order = mapToOrder(dto);
        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            ProductDTO product = productClient.getProductById(item.getProductId())
                    .orElseThrow(() -> {
                        orderLogService.logProductNotFound(item.getProductId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Product not found: " + item.getProductId());
                    });

            if (product.getQuantity() < item.getQuantity()) {
                orderLogService.logOrderRejected(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Not enough stock for product: " + product.getName());
            }

            item.setPricePerUnit(product.getPrice());
            item.setProductName(product.getName());
            item.setDescription(product.getDescription());

            BigDecimal itemTotal = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalSum = totalSum.add(itemTotal);
        }

        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CREATED");
        order.setTotalAmount(totalSum);

        productClient.decrementProductQuantities(order.getItems().stream()
                .map(i -> new ProductDecrementRequest(i.getProductId(), i.getQuantity()))
                .toList());

        Order savedOrder = repository.save(order);

        orderLogService.logOrderCreated(savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getCreatedAt());

        if (email != null && !email.isBlank()) {
            try {
                orderNotificationSender.sendOrderNotification(new OrderNotificationDTO(email,
                        savedOrder));
            } catch (Exception e) {
                System.err.println("Failed to send order notification: " + e.getMessage());
                e.printStackTrace();
            }

        }

        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

}