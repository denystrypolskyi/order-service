package com.example.order_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.order_service.OrderNotificationSender;
import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.*;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderNotificationSender orderNotificationSender;
    @Mock
    private OrderLogService orderLogService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_success() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setUserId(1L);
        OrderItemDTO item = new OrderItemDTO(100L, 2);
        dto.setItems(List.of(item));

        ProductDTO product = new ProductDTO(100L, "Product A", "Desc", BigDecimal.valueOf(10), 10);
        when(productClient.getProductById(100L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(dto, "test@example.com");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
        verify(productClient).decrementProductQuantities(any());
        verify(orderNotificationSender).sendOrderNotification(any());
        verify(orderLogService).logOrderCreated(result.getId(), result.getTotalAmount(), result.getCreatedAt());
    }

    @Test
    void testCreateOrder_notEnoughStock_throwsException() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setUserId(1L);
        OrderItemDTO item = new OrderItemDTO(100L, 5);
        dto.setItems(List.of(item));

        ProductDTO product = new ProductDTO(100L, "Product A", "Desc", BigDecimal.valueOf(10), 2);
        when(productClient.getProductById(100L)).thenReturn(Optional.of(product));

        assertThrows(ResponseStatusException.class, () -> {
            orderService.createOrder(dto, "test@example.com");
        });
    }

    @Test
    void testDeleteOrder_orderExists_deletesSuccessfully() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L, 1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void testDeleteOrder_orderNotFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            orderService.deleteOrder(1L, 1L);
        });
    }
}
