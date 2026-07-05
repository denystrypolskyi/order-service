# Order Service

This microservice manages orders. It allows creating, retrieving, and deleting orders. It also integrates with the Product Service to verify stock and decrement product quantities, and sends order notifications via RabbitMQ.

## Features

- Get all orders  
- Get orders by the current authenticated user  
- Create a new order (validates product availability, decrements stock)  
- Delete an order (only by the owner)  
- Sends order notifications via RabbitMQ  

## Endpoints

| HTTP Method | Endpoint         | Description                      | Authentication Required |
|-------------|------------------|--------------------------------|------------------------|
| GET         | `/orders`        | Get all orders                  | Yes                    |
| GET         | `/orders/my`     | Get orders of the current user | Yes                    |
| POST        | `/orders`        | Create a new order              | Yes                    |
| DELETE      | `/orders/{orderId}` | Delete an order by ID          | Yes                    |

## Security

- All `/orders/**` endpoints require a valid JWT bearer token.
- The authenticated user ID is read from the token, not trusted from the request body.

## Tech Stack

- Java 21
- Spring Boot  
- Jakarta Validation  
- RabbitMQ (spring-amqp)  
- Lombok  
- PostgreSQL  
- Flyway
