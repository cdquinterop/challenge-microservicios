# Challenge Microservicios - Solución Técnica

Este proyecto implementa una arquitectura de microservicios basada en Spring Boot, que permite la gestión de clientes y cuentas bancarias, incluyendo operaciones de creación, actualización, consulta y eliminación, utilizando PostgreSQL como base de datos y RabbitMQ para comunicación asíncrona entre servicios.

## Tabla de contenidos

1. [Arquitectura](#arquitectura)
2. [Levantamiento con Docker](#levantamiento-con-docker)
3. [Colección Postman](#colección-postman)
4. [Ejecución de Tests](#ejecución-de-tests)
5. [Tecnologías](#tecnologías)
6. [Repositorio](#repositorio)

---

## Arquitectura

El sistema está compuesto por los siguientes microservicios:

- **persona-service**: Maneja información de clientes (crear, actualizar, consultar, eliminar lógicamente).
- **cuenta-service**: Administra cuentas bancarias asociadas a los clientes y registra movimientos.
- **RabbitMQ**: Utilizado para la comunicación asíncrona de eventos (cliente creado, actualizado, eliminado).
- **PostgreSQL**: Base de datos relacional para persistencia.

---

## Levantamiento con Docker

### Instrucciones

1. Asegúrate de tener [Docker](https://docs.docker.com/get-docker/) y [Docker Compose](https://docs.docker.com/compose/) instalados.
2. Ejecuta el siguiente comando desde la raíz del proyecto:

```bash
docker-compose up --build
```

Esto iniciará:

- PostgreSQL en el puerto **5432**
- RabbitMQ en el puerto **5672** (interfaz en [http://localhost:15672](http://localhost:15672))
- Microservicios: **persona-service** y **cuenta-service**

### Credenciales RabbitMQ

```makefile
Usuario: guest
Contraseña: guest
```

---

## Colección Postman

Se incluye una colección para probar los endpoints de ambos microservicios:

- Archivo: `CHALLENGE-PRUEBA.postman_collection`

Importa esta colección en Postman y modifica las variables si es necesario para apuntar a los puertos locales.

---

## Ejecución de Tests

Los servicios están cubiertos con pruebas unitarias e integración.

Para ejecutarlos manualmente desde el proyecto:

```bash
./mvnw clean test
```

Asegúrate de tener RabbitMQ y PostgreSQL corriendo.

---

## Tecnologías

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Docker & Docker Compose
- Lombok, ModelMapper, JUnit 5

---

## Repositorio

Puedes encontrar todo el código fuente y documentación adicional en:

🔗 [https://github.com/cdquinterop/challenge-microservicios](https://github.com/cdquinterop/challenge-microservicios)
