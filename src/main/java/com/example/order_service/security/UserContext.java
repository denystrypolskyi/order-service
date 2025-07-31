package com.example.order_service.security;

public class UserContext {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> emailHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    public static Long getUserId() {
        return userIdHolder.get();
    }

    public static void setEmail(String email) {
        emailHolder.set(email);
    }

    public static String getEmail() {
        return emailHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
        emailHolder.remove();
        tokenHolder.remove();
    }
}
