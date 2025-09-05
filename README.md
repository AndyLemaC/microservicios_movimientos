# ⚡ Prueba Técnica – Microservicios Clientes & Movimientos  

Este proyecto implementa dos microservicios en **Java Spring Boot** que gestionan **Clientes** y **Cuentas con Movimientos**, con comunicación asincrónica mediante RabbitMQ, base de datos relacional en PostgreSQL y despliegue en Docker.  

---

## ✅ Requisitos Previos

- **Java 17**  
- **Maven 3.9+**  
- **Docker y Docker Compose**  
- **Postman** v9.13.2 (para validación de APIs)  
- IDE de preferencia (IntelliJ, Eclipse, VS Code, etc.)  

---

## ⚙️ Arquitectura de la Solución

### Separación por microservicios
- **Microservicio Clientes**  
  - Maneja `Persona` y `Cliente`  
  - Expone CRUD de clientes  
  - Atiende solicitudes RPC desde el microservicio Movimientos vía RabbitMQ  

- **Microservicio Movimientos**  
  - Maneja `Cuenta` y `Movimiento`  
  - CRUD de cuentas y movimientos  
  - Registra movimientos y actualiza saldos  
  - Consulta datos adicionales de clientes vía RabbitMQ (ej. nombreCliente en reportes)  

### Comunicación
- **RabbitMQ** → Asincrónica, con colas y binding configurados para:
  - Validación de existencia de clientes  
  - Obtención de nombre de cliente  

### Persistencia
- **PostgreSQL** → Relacional, con JPA/Hibernate para el mapeo de entidades.  
- Se utilizan repositorios con **Spring Data JPA** siguiendo patrón Repository.  

---

## 📁 Estructura del Proyecto

```
prueba_tecnica/
├── microservicioclientes/
│   ├── src/main/java/com/proyecto/microservicioclientes
│   │   ├── entities/Persona.java, Cliente.java
│   │   ├── repository/ClienteRepository.java
│   │   ├── service/ClienteService.java
│   │   ├── message/ClienteRpc.java  # @RabbitListener
│   │   └── config/RabbitConfig.java
│   └── Dockerfile
│
├── microserviciomovimientos/
│   ├── src/main/java/com/proyecto/microserviciomovimientos
│   │   ├── entities/Cuenta.java, Movimiento.java
│   │   ├── repository/CuentaRepository.java, MovimientoRepository.java
│   │   ├── service/MovimientoServiceImpl.java
│   │   ├── message/ClienteProducer.java  # RPC hacia microservicioclientes
│   │   └── config/RabbitConfig.java
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

## 🐳 Despliegue con Docker

1. Clonar repositorio y entrar a la carpeta:  
   ```bash
   git clone https://github.com/AndyLemaC/microservicios_movimientos.git
   cd prueba_tecnica
   ```

2. Levantar servicios:  
   ```bash
   docker compose up -d --build
   ```

3. Verificar contenedores:  
   ```bash
   docker ps
   ```

4. Detener y eliminar por completo todos los recursos de Docker Compose:  
   ```bash
   docker compose down --rmi local --volumes --remove-orphans
   ```

- **Clientes API** → `http://localhost:8080`  
- **Movimientos API** → `http://localhost:8081`  
- **RabbitMQ Management** → `http://localhost:15672` (guest/guest)  
- **Postgres** → `localhost:5433`  

---

## 📌 Ejemplos de Endpoints

### Crear cliente
```http
POST http://localhost:8080/clientes
Content-Type: application/json

{
  "nombre": "Juan Perez",
  "genero": "M",
  "edad": 30,
  "identificacion": "123456",
  "direccion": "Av. Principal 123",
  "telefono": "0999999999",
  "contrasena": "1234",
  "estado": true
}
```

### Crear cuenta
```http
POST http://localhost:8081/cuentas
{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorro",
  "saldoInicial": 2000.00,
  "estado": true,
  "clienteId": 1
}
```

### Registrar movimiento
```http
POST http://localhost:8081/movimientos
{
  "numeroCuenta": "478758",
  "tipoMovimiento": "Retiro",
  "valor": -575
}
```

### Reporte de estado de cuenta
```http
GET http://localhost:8081/reportes?clienteId=1&fechaInicio=2025-09-01&fechaFin=2025-09-05
```

---

## 🧪 Pruebas Unitarias & Integración

Se implementaron dos tipos de pruebas con **JUnit 5**:

### ▶️ Test Unitario (Microservicio Clientes)
Este test valida la lógica de negocio de la entidad **Cliente** de manera aislada.

```bash
cd microservicioclientes
mvn -Dtest=ClienteServiceTest test
```

📷 **Resultado:**  
![Test Unitario Cliente](Captura%20de%20pantalla%202025-09-04%20213814.png)

---

### ▶️ Test de Integración (Microservicio Movimientos)
Este test valida el flujo completo de **registro de movimientos**, con persistencia en base H2 y mock de RPC.

```bash
cd microserviciomovimientos
mvn clean test "-Dspring.profiles.active=test" "-Dtest=MovimientoFlowIntegrationTest"
```

📷 **Resultado:**  
![Test Integración Movimientos](Test%20Integración.png)
```

---
