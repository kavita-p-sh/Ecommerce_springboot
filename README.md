E-Commerce API
______________________
This is a Spring Boot based REST API for managing users, products, and orders.
The application supports authentication, role-based authorization, and basic e-commerce operations.
_________________________________________________________________________________________________________
Features
-> User Registration and Login (JWT Authentication)
-> Role-based Access Control (Admin, User)
-> Product Management (Create, Update, Delete, Fetch)
-> Order Management (Place, View, Cancel Orders)
-> Input Validation using DTOs
-> Caching using Spring Cache (Redis)
-> Global Exception Handling
-> Audit fields (createdBy, updatedBy, timestamps)
___________________________________________________________________________________________________________
Technologies Used
Java
Spring Boot
Spring Security
Spring Data JPA
MySQL
Maven
Redis (for caching)
JWT (Authentication)
___________________________________________________________________________________________________________

Architecture
The application follows a layered architecture:

Controller → Service → Repository → Database
___________________________________________________________________________________________________________

Design Patterns
Dependency Injection
Repository Pattern
DTO Pattern
___________________________________________________________________________________________________________

Project Structure
src/main/java/com/ecommerce/api/
├── audit/         # Auditing (createdBy, updatedBy, timestamps)
├── config/        # Configuration classes
├── controller/    # REST controllers
├── dto/           # Data Transfer Objects
├── entity/        # JPA entities
├── enums/         # Enum classes
├── exception/     # Global exception handling
├── repository/    # JPA repositories
├── security/      # JWT & Spring Security
├── service/       # Business logic
├── util/          # Utility classes
└── EcommerceApplication.java
___________________________________________________________________________________________________________

src/main/resources/
├── application.properties
├── application-dev.properties
├── application-prod.properties
├── data.sql
___________________________________________________________________________________________________________

Setup Instructions
Database Setup (MySQL)

Create a database before running the application:
CREATE DATABASE ecom_db;

Linux / Mac
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JWT_SECRET=$(openssl rand -base64 32)

Windows (CMD)
Set Environment Variables
set DB_USERNAME=your_db_user
set DB_PASSWORD=your_db_password
set JWT_SECRET=your_secret_key
___________________________________________________________________________________________________________
## Configure application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/ecom_db

# Database credentials
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# JWT secret (256-bit Base64 key)
JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
## JWT Secret Key
For local development, generate a Base64-encoded 256-bit secret key before running the application.

spring.redis.host=localhost
spring.redis.port=6379

___________________________________________________________________________________________________________

Run Application
mvn spring-boot:run
___________________________________________________________________________________________________________

Authentication Flow
Register or Login user
Receive JWT token
Use token in headers: Authorization: Bearer <your_token>
___________________________________________________________________________________________________________
Swagger / API Documentation
Swagger is integrated for API documentation and testing.

After starting the application, open:
Swagger UI:
http://localhost:8094/swagger-ui/index.html
OpenAPI Docs:
http://localhost:8094/v3/api-docs

View all APIs
Test endpoints directly
Check request/response formats
___________________________________________________________________________________________________________

Main APIs
Auth
POST /auth/register
POST /auth/login
___________________________________________________________________________________________________________

Users
GET /api/users
GET /api/users/profile
PUT /api/users
PUT /api/users/profile
DELETE /api/users/{username}
___________________________________________________________________________________________________________

Products
POST /api/products
GET /api/products
PUT /api/products/{id}
DELETE /api/products/{id}
___________________________________________________________________________________________________________

Orders
POST /api/orders
GET /api/orders
PUT /api/orders/cancel/{orderId}
___________________________________________________________________________________________________________

Database Tables
users
roles
products
orders
order_items
order_status
___________________________________________________________________________________________________________
Security

JWT-based authentication
Role-based authorization
Password encryption using BCrypt
___________________________________________________________________________________________________________
Caching

Redis is used for caching frequently accessed data
Improves performance of product and order APIs
Cache is updated on create/update/delete operations
___________________________________________________________________________________________________________

Profiles
application-dev.properties -->  Development environment
application-prod.properties --> Production environment
