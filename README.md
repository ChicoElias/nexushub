🇪🇸 Spanish version: "README_ES.md" (./README_ES.md)

NexusHub

Mobile-first product management system built with Android (Kotlin + Jetpack Compose) and a Spring Boot REST API.

Overview

NexusHub is a fullstack project that demonstrates integration between a native Android application and a backend API.
It simulates a product management system for small businesses, focusing on clean architecture and real development practices.

Tech Stack

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

Database

- H2 (development)
- MySQL (configurable)

Features

- User registration and basic login
- Product CRUD operations
- Product listing and detail view
- Data validation and error handling
- Integration between mobile app and backend API

Architecture

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

API Endpoints

Auth

- POST /auth/register
- POST /auth/login

Products

- GET /products
- GET /products/{id}
- POST /products
- PUT /products/{id}
- DELETE /products/{id}

How to Run Backend

mvn spring-boot:run

Swagger:

http://localhost:8080/swagger-ui/index.html

How to Run Mobile App

1. Open in Android Studio
2. Run emulator or device
3. Ensure backend is running

Future Improvements

- JWT authentication
- Product filtering
- User-product relation
- Pagination

Author

Elías Delgado Manríquez
GitHub: https://github.com/ChicoElias
