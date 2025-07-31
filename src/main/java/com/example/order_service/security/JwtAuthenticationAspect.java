package com.example.order_service.security;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.order_service.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class JwtAuthenticationAspect {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    @Before("@annotation(com.example.order_service.security.Authenticated)")
    public void authenticate() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        UserContext.setToken(token);
        try {
            if (jwtUtil.isTokenExpired(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
            }

            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);

            if (userId == null || email == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }

            UserContext.setUserId(userId);
            UserContext.setEmail(email);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    @After("@annotation(com.example.order_service.security.Authenticated)")
    public void clearUserContext() {
        UserContext.clear();
    }
}
