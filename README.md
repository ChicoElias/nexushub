# NexusHub

NexusHub is a fullstack portfolio project built around a Spring Boot backend and an Android client. It simulates a product catalog and inventory management workflow for small sellers who need a lightweight way to publish listings, track stock, and browse products from a mobile app.

## Why This Project Matters

This repository is useful in a portfolio because it connects two skills that are often shown separately:

- backend API design with Java and Spring Boot
- Android app development with Kotlin and Jetpack Compose

Instead of presenting isolated exercises, NexusHub shows how a mobile client can consume a custom API with realistic business rules such as stock transitions, visibility control, and ownership validation.

## Repository Structure

```text
nexushub/
|-- nexushub-backend
`-- nexushub-android
```

## Backend Highlights

- Java 17 + Spring Boot 3.2
- layered architecture with controller, service, repository, dto, entity, config, and exception packages
- Spring Data JPA and Hibernate
- H2 for local development and optional MySQL configuration
- Swagger / OpenAPI documentation
- input validation and centralized error handling
- seeded demo data for quick local testing

## Android Highlights

- Kotlin + Jetpack Compose
- MVVM + StateFlow
- Retrofit + OkHttp for API integration
- DataStore for local session persistence
- Material 3 UI with reusable components

## Key Features

- user registration and login
- product creation, update, and soft deletion
- automatic product status transitions based on stock
- full-text search and category filtering
- paginated product listings
- product statistics endpoint
- mobile catalog browsing and detail view

## Product Status Rules

- products with stock greater than zero start as `ACTIVE`
- products with zero stock become `OUT_OF_STOCK`
- sellers can manually set products to `INACTIVE`
- products cannot be activated if stock is zero

## Running The Backend

Prerequisites:

- Java 17+
- Maven 3.8+

Commands:

```bash
cd nexushub-backend
mvn spring-boot:run
```

Useful URLs:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
- OpenAPI JSON: `http://localhost:8080/api-docs`

Demo accounts:

- `alice@nexushub.dev` / `password123`
- `bob@nexushub.dev` / `password123`

## Running The Android App

Prerequisites:

- Android Studio Hedgehog or newer
- Android emulator or physical device

Steps:

1. Open `nexushub-android` in Android Studio.
2. Wait for Gradle sync to finish.
3. Run the app on an emulator or device.
4. Keep the backend running locally.

The emulator base URL is configured for `http://10.0.2.2:8080`.

## Portfolio Value

NexusHub helps demonstrate:

- backend and mobile integration
- API contract design
- real-world CRUD rules beyond basic demos
- inventory state handling
- a clean separation between client and server responsibilities

## Future Improvements

- JWT-based authentication
- image upload support
- Dockerized backend environment
- automated tests for the backend module
- CI for both backend and Android modules

## License

This project is licensed under the MIT License.
