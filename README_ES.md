🇺🇸 English version: "README.md" (./README.md)

NexusHub

Sistema de gestión de productos orientado a dispositivos móviles, desarrollado con Android (Kotlin + Jetpack Compose) y un backend en Spring Boot.

Descripción

NexusHub es un proyecto fullstack que demuestra la integración entre una aplicación Android nativa y una API backend.
Simula la gestión de productos para pequeños negocios, aplicando buenas prácticas de desarrollo y arquitectura limpia.

Stack Tecnológico

Mobile

- Kotlin
- Jetpack Compose
- MVVM
- Retrofit

Backend

- Java
- Spring Boot
- Spring Data JPA
- Maven

Base de Datos

- H2 (desarrollo)
- MySQL (configurable)

Funcionalidades

- Registro y login básico
- CRUD de productos
- Listado y detalle de productos
- Validación de datos
- Integración entre app móvil y backend

Arquitectura

Backend

- Controller
- Service
- Repository
- DTO
- Entity
- Exception Handler

Mobile

- UI (Jetpack Compose)
- ViewModel
- Repository
- API Service (Retrofit)

Endpoints API

Autenticación

- POST /auth/register
- POST /auth/login

Productos

- GET /products
- GET /products/{id}
- POST /products
- PUT /products/{id}
- DELETE /products/{id}

Cómo ejecutar Backend

mvn spring-boot:run

Swagger:

http://localhost:8080/swagger-ui/index.html

Cómo ejecutar App

1. Abrir en Android Studio
2. Ejecutar en emulador o dispositivo
3. Asegurar backend activo

Mejoras futuras

- Autenticación JWT
- Filtros
- Relación usuario-producto
- Paginación

Autor

Elías Delgado Manríquez
GitHub: https://github.com/ChicoElias
