# Microservicio de Salas - Hospital DuocQuin 🏥

Administra la infraestructura física, salas de consulta y pabellones del centro hospitalario.

## 🛠️ Tecnologías
- **Java 17**
- **Spring Boot 3.x**
- **MySQL**
- **Maven**

## 📋 Funcionalidades
- **Inventario de Salas**: Registro de tipos de sala (Box, Quirófano, Dental, etc.).
- **Control de Capacidad**: Gestión de la ocupación máxima permitida por área.
- **Disponibilidad**: Consulta de estado de salas para la asignación de horarios médicos.

## ⚙️ Configuración y Ejecución
1. Configurar la base de datos MySQL.
2. Actualizar `src/main/resources/application.properties`.
3. Ejecutar el servicio:
```bash
./mvnw spring-boot:run
```
El servicio estará disponible en `http://localhost:8083`.

## 📡 API Endpoints Principales
- `GET /api/salas`: Listar todas las salas.
- `POST /api/salas`: Crear nueva sala clínica.
- `PUT /api/salas/{id}`: Actualizar capacidad o tipo.
- `DELETE /api/salas/{id}`: Eliminar sala del sistema.
