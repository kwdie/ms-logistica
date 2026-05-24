# Microservicio Logística - VetNova
Microservicio encargado de administrar los proveedores del sistema VetNova.

## Tecnologías utilizadas
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Lombok

## Funcionalidades
- Listar proveedores
- Buscar proveedores por ID
- Registrar proveedores
- Actualizar proveedores
- Eliminar proveedores

## Puerto utilizado
8087

## Base de datos
logistica_db

## Endpoints principales
### Listar proveedores
GET /proveedores

### Buscar proveedor por ID
GET /proveedores/{id}

### Registrar proveedor
POST /proveedores

### Actualizar proveedor
PUT /proveedores/{id}

### Eliminar proveedor
DELETE /proveedores/{id}

## Validaciones implementadas
- Nombre obligatorio
- Empresa obligatoria
- Teléfono obligatorio
- Correo válido y obligatorio

## Manejo de excepciones
- Proveedor no encontrado
- Errores de validación
- Error interno del servidor

## Logs implementados
El microservicio registra:
- Listado de proveedores
- Búsquedas por ID
- Registro de proveedores
- Actualización de proveedores
- Eliminación de proveedores
- Errores del sistema

## Ejecución del proyecto
```bash
mvn spring-boot:run
```
## Autor
Diego Torres
