
# Microservicios - Ejercicio Técnico

Este proyecto implementa dos microservicios independientes: uno para la gestión de **Clientes** y otro para la gestión de **Cuentas y Movimientos**. Ambos servicios están diseñados para comunicarse de forma asincrónica mediante **RabbitMQ**.

---

## Requisitos Previos

### Tecnologías necesarias
- **Java 17** o superior.
- **Maven** para la gestión de dependencias.
- **RabbitMQ** como sistema de mensajería.
- **Postman** para probar los endpoints.


---

## Configuración

### 1. RabbitMQ

Asegúrate de que RabbitMQ esté instalado y corriendo.

**Credenciales predeterminadas:**
- **Host**: `localhost`
- **Puerto**: `5672`
- **Usuario**: `guest`
- **Contraseña**: `guest`

Si necesitas cambiar estas configuraciones, actualiza el archivo `application.properties` en ambos microservicios:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest


