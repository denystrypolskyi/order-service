package com.example.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.order_service.OrderNotificationSender;
import com.example.order_service.client.ProductClient;
import com.example.order_service.client.UserClient;
import com.example.order_service.dto.OrderCreateDTO;
import com.example.order_service.dto.OrderNotificationDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.dto.ProductDecrementRequest;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final OrderNotificationSender orderNotificationSender;
    private final UserClient userClient;

    public OrderService(OrderRepository repository, ProductClient productClient,
            OrderNotificationSender orderNotificationSender, ObjectMapper objectMapper, UserClient userClient) {
        this.repository = repository;
        this.productClient = productClient;
        this.orderNotificationSender = orderNotificationSender;
        this.userClient = userClient;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));

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
    public Order createOrder(OrderCreateDTO dto) {
        Order order = mapToOrder(dto);
        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            ProductDTO product = productClient.getProductById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found: " + item.getProductId()));

            if (product.getQuantity() < item.getQuantity()) {
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

        String email = "";
        if (email == null || email.isBlank()) {
            try {
                email = userClient.getUserEmailById(savedOrder.getUserId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (email != null && !email.isBlank()) {
            try {
                orderNotificationSender.sendOrderNotification(new OrderNotificationDTO(email, savedOrder));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savedOrder;
    }

}
