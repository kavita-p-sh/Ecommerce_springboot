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
Create Database
CREATE DATABASE ecommerce_db;
___________________________________________________________________________________________________________
Configure application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

JWT Configuration
jwt.secret=your_secret_key
jwt.expiration=3600000

Redis Configuration
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

Main APIs
Auth
POST /api/auth/register
POST /api/auth/login
___________________________________________________________________________________________________________

Users
GET /api/users
GET /api/users/profile
PUT /api/users/profile
___________________________________________________________________________________________________________

Products
POST /api/products
GET /api/products
PUT /api/products/{name}
DELETE /api/products/{name}
___________________________________________________________________________________________________________

Orders
POST /api/orders
GET /api/orders
PUT /api/orders/{orderId}/cancel
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
