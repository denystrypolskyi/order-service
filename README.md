# Order Service

This microservice manages orders. It allows creating, retrieving, and deleting orders. It also integrates with the Product Service to verify stock and decrement product quantities, and sends order notifications via RabbitMQ. Order-related and product-related events are logged to Kafka.

## 🚀 Features

- Get all orders  
- Get orders by the current authenticated user  
- Create a new order (validates product availability, decrements stock)  
- Delete an order (only by the owner)  
- Sends order notifications via RabbitMQ  
- Logs order and product events to Kafka  

## 📍 Endpoints

| HTTP Method | Endpoint         | Description                      | Authentication Required |
|-------------|------------------|--------------------------------|------------------------|
| GET         | `/orders`        | Get all orders                  | Yes                    |
| GET         | `/orders/my`     | Get orders of the current user | Yes                    |
| POST        | `/orders`        | Create a new order              | Yes                    |
| DELETE      | `/orders/{orderId}` | Delete an order by ID          | Yes                    |

## 🔐 Security

- All endpoints require authentication via a custom `@Authenticated` annotation.
- In production, role-based access control would be added to restrict sensitive operations.

## 📦 Tech Stack

- Java 24  
- Spring Boot  
- Jakarta Validation  
- RabbitMQ (spring-amqp)  
- Kafka  
- Lombok  
- PostgreSQL  
