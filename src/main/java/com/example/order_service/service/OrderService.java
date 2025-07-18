package com.example.order_service.service;

import com.example.order_service.OrderNotificationSender;
import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.dto.ProductDecrementRequest;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final OrderNotificationSender orderNotificationSender;

    public OrderService(OrderRepository repository, ProductClient productClient,
            OrderNotificationSender orderNotificationSender) {
        this.repository = repository;
        this.productClient = productClient;
        this.orderNotificationSender = orderNotificationSender;
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

    @Transactional
    public Order createOrder(Order order) {
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

            BigDecimal itemTotal = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalSum = totalSum.add(itemTotal);

            item.setOrder(order);
        }

        if (order.getCreatedAt() == null) {
            order.setCreatedAt(java.time.LocalDateTime.now());
        }
        if (order.getStatus() == null) {
            order.setStatus("CREATED");
        }

        order.setTotalAmount(totalSum);

        // decrement product quantity
        List<ProductDecrementRequest> decrementRequests = order.getItems().stream()
                .map(item -> new ProductDecrementRequest(item.getProductId(), item.getQuantity()))
                .toList();
        productClient.decrementProductQuantities(decrementRequests);

        // save order to db
        Order savedOrder = repository.save(order);

        // send notification
        orderNotificationSender.sendOrderNotification("Новый заказ #" + order.getId() + " создан");

        return savedOrder;
    }
}
