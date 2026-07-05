# Order Service

Order service for the e-commerce backend. It creates orders, validates product availability through the Product Service, decrements stock, and publishes order notifications to RabbitMQ.

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

- Order creation reserves product stock through the Product Service.
- External Product Service calls have explicit connection and read timeouts.

## Run Tests

```powershell
.\mvnw.cmd test
```
