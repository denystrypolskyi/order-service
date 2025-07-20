package com.example.order_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.dto.UserEmailDTO;

@Service
public class UserClient {
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Optional<String> getUserEmailById(Long userId) {
        try {
            UserEmailDTO user = restTemplate.getForObject(userServiceUrl + "/users/" + userId, UserEmailDTO.class);
            return Optional.ofNullable(user.getEmail());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
