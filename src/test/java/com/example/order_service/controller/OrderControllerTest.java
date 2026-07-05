package com.example.order_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.order_service.dto.OrderCreateDTO;
import com.example.order_service.jwt.JwtUtil;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.security.UserContext;
import com.example.order_service.service.OrderService;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void createOrderUsesAuthenticatedUserId() throws Exception {
        UserContext.setUserId(7L);
        UserContext.setEmail("buyer@example.com");

        Order order = new Order();
        order.setId(10L);
        order.setUserId(7L);
        order.setStatus("CREATED");
        order.setTotalAmount(BigDecimal.valueOf(50));

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setProductId(100L);
        item.setProductName("Mouse");
        item.setDescription("Wireless mouse");
        item.setQuantity(2);
        item.setPricePerUnit(BigDecimal.valueOf(25));
        item.setOrder(order);
        order.setItems(List.of(item));

        when(orderService.createOrder(any(OrderCreateDTO.class), any(Long.class), any(String.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "userId": 999,
                          "items": [
                            { "productId": 100, "quantity": 2 }
                          ]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(7));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderService).createOrder(any(OrderCreateDTO.class), userIdCaptor.capture(), any(String.class));
        org.junit.jupiter.api.Assertions.assertEquals(7L, userIdCaptor.getValue());
    }
}
