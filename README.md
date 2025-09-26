## Overview
This project demonstrates a complete implementation of the Saga Orchestration Pattern using Spring Boot, PostgreSQL, and RabbitMQ. It simulates an e-commerce order processing system where distributed transactions are coordinated across multiple microservices.

## Architecture

### Services
- Order Service (Port 8080) - Creates orders and orchestrates sagas
- Payment Service (Port 8081) - Processes payments and handles refunds
- Inventory Service (Port 8082) - Manages product inventory and reservations
- Notification Service (Port 8083) - Sends customer notifications

### Infra

- PostgreSQL - Individual databases for each service
- RabbitMQ - Message broker with topic exchanges and queues 
- Flyway - Database migration management
- Spring Boot Actuator - Health checks and monitoring
- Promosthues and Grafana - Monitoring