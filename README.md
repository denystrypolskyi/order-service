# Order Service

Order service for the distributed backend system. It creates orders, validates product availability through the Product Service, decrements stock, and publishes order notifications to RabbitMQ.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- RabbitMQ
- Bean Validation
- JUnit, Mockito, MockMvc

## API

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/orders` | Yes | List all orders |
| `GET` | `/orders/my` | Yes | List orders for the authenticated user |
| `POST` | `/orders` | Yes | Create a new order |
| `DELETE` | `/orders/{orderId}` | Yes | Delete an order owned by the authenticated user |

### Create Order Request

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

## Messaging

When an order is created, the service publishes a message to RabbitMQ queue:

```text
order.notifications
```

The Notification Service consumes this message and sends the email notification.

## Notes

- All endpoints require a valid JWT bearer token.
- The user ID is read from the JWT, not from the request body.
- Calls to the Product Service use HTTP timeouts.
- API errors are returned through a centralized exception handler.

## Run Tests

```powershell
.\mvnw.cmd test
```
