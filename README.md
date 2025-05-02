# Microservices Challenge – Technical Solution

This project implements a microservices architecture based on Spring Boot, enabling the management of customers and bank accounts. It supports operations such as creation, updating, querying, and logical deletion, using PostgreSQL as the database and RabbitMQ for asynchronous communication between services.

## Table of Contents

1. [Architecture](#architecture)  
2. [Docker Setup](#docker-setup)  
3. [Postman Collection](#postman-collection)  
4. [Test Execution](#test-execution)  
5. [Technologies](#technologies)  
6. [Repository](#repository)

---

## Architecture

The system consists of the following microservices:

- **person-service**: Manages customer information (create, update, retrieve, logically delete).
- **account-service**: Handles bank accounts associated with customers and records account transactions.
- **RabbitMQ**: Used for asynchronous communication of events (customer created, updated, deleted).
- **PostgreSQL**: Relational database used for persistence.

---

## Docker Setup

### Instructions

1. Ensure you have [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/) installed.
2. From the root of the project, run the following command:

```bash
docker-compose up --build
```

This will start:

- PostgreSQL on port **5432**
- RabbitMQ on port **5672** (management UI at [http://localhost:15672](http://localhost:15672))
- Microservices: **person-service** and **account-service**

### RabbitMQ Credentials

```makefile
Username: guest  
Password: guest
```

---

## Postman Collection

A collection is provided to test the endpoints of both microservices:

- File: `CHALLENGE-PRUEBA.postman_collection`

Import this collection into Postman and update environment variables if necessary to point to your local ports.

---

## Test Execution

Both services include unit and integration tests.

To run them manually from the project:

```bash
./mvnw clean test
```

Make sure RabbitMQ and PostgreSQL are running.

---

## Technologies

- Java 17  
- Spring Boot 3.4.0  
- Spring Data JPA  
- Spring AMQP (RabbitMQ)  
- PostgreSQL  
- Docker & Docker Compose  
- Lombok, ModelMapper, JUnit 5

---

## Repository

You can find the complete source code and additional documentation at:

🔗 [https://github.com/cdquinterop/challenge-microservicios](https://github.com/cdquinterop/challenge-microservicios)