package com.example.order_service.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.dto.ProductDTO;
import com.example.order_service.dto.ProductDecrementRequest;

import jakarta.validation.Valid;

@Service
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Optional<ProductDTO> getProductById(Long productId) {
        try {
            return Optional.ofNullable(
                    restTemplate.getForObject(productServiceUrl + "/products/" + productId, ProductDTO.class));
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    public void decrementProductQuantities(List<ProductDecrementRequest> requests) {
        try {
            restTemplate.put(productServiceUrl + "/products/decrement-batch", requests);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to decrement product quantities", e);
        }
    }

}
