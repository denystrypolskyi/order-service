package com.example.order_service.client;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.order_service.dto.ProductDTO;
import com.example.order_service.dto.ProductDecrementRequest;
import com.example.order_service.security.UserContext;

@Service
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
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
            String token = UserContext.getToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<List<ProductDecrementRequest>> entity = new HttpEntity<>(requests, headers);

            restTemplate.exchange(
                    productServiceUrl + "/products/decrement-batch",
                    HttpMethod.PUT,
                    entity,
                    Void.class);

        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Failed to decrement product quantities", e);
        }
    }
}
