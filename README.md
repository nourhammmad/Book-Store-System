# 📚 Book Store Management System

The **Book Store Management System** is a backend application built with **Java** and **Spring Boot** that provides a secure and scalable way to manage a bookstore.  

It covers the core workflows of a modern bookstore, including:
- 🔑 **Authentication & Authorization** with JWT (Customers & Admins)  
- 📚 **Book Management** – add, update, view, and delete books  
- 👤 **Customer Management** – customer profiles & accounts  
- 🛒 **Order & Order Items** – place orders, track items status, manage inventory  

This project is designed with **RESTful API principles**, **Spring Security**, and **JPA/Hibernate** for database interaction.  
It supports **in-memory H2** .  

Whether you are a **customer** browsing and purchasing books or an **admin** managing inventory and users, this system provides a structured and secure backend for bookstore operations.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-blue)](https://spring.io/projects/spring-boot)

**Spring Boot–powered Book Store REST API** with JWT security, role-based access control, and full CRUD management of books, users, and orders.

---

##  Table of Contents

1. [Features](#-features)  
2. [Technology Stack](#-technology-stack)  
3. [Getting Started](#-getting-started)  
4. [API Endpoints](#-api-endpoints)  
5. [Authentication Flow](#-authentication-flow)  
6. [Testing](#-testing)  
7. [Project Structure](#-project-structure)  
8. [Contributing](#-contributing)  
9. [Author](#-author)  

---

##  Features

- **Authentication & Authorization**
  - JWT-based stateless authentication  
  - Role-based access (Admin vs Customer)
- **Book Management**
  - CRUD operations: add, update, delete, list books
- **Order Management**
  - Place orders and manage order details
- **User Management**
  - Registration, login, profile management
- **Documentation**
  - Integrated Swagger UI for interactive API exploration
- **Secure Password Storage**
  - Passwords hashed with BCrypt

---

##  Technology Stack

| Layer               | Technology                                      |
|--------------------|--------------------------------------------------|
| Framework           | Spring Boot (REST API)                           |
| Security            | Spring Security with JWT                         |
| Persistence         | Spring Data JPA (H2 in-memory for development)   |
| Build Tool          | Maven                                            |
| API Documentation   | Swagger UI                                       |

---

##  Getting Started

### Prerequisites

- Java 17 (or above)  
- Maven
## 📖 API Endpoints

### 🔑 Authentication
| Method | Endpoint         | Description              | Access |
|--------|------------------|--------------------------|--------|
| POST   | `/auth/register` | Register a new customer | Public |
| POST   | `/auth/login`    | Authenticate & get JWT  | Public |

---

### 📚 Books
| Method | Endpoint       | Description          | Access   |
|--------|----------------|----------------------|----------|
| GET    | `/books`       | Get all books        | Public   |
| GET    | `/books/{id}`  | Get book by ID       | Public   |
| POST   | `/books`       | Add a new book       | Admin    |
| PUT    | `/books/{id}`  | Update book details  | Admin    |
| DELETE | `/books/{id}`  | Delete a book        | Admin    |

---

### 👤 Customers
| Method | Endpoint           | Description               | Access   |
|--------|--------------------|---------------------------|----------|
| GET    | `/customers`       | Get all customers         | Admin    |
| GET    | `/customers/{id}`  | Get customer by ID        | Admin    |
| PUT    | `/customers/{id}`  | Update customer profile   | Customer |
| DELETE | `/customers/{id}`  | Delete customer account   | Admin    |

---

### 🛒 Orders
| Method | Endpoint           | Description               | Access   |
|--------|--------------------|---------------------------|----------|
| POST   | `/orders`          | Place a new order         | Customer |
| GET    | `/orders`          | Get all orders            | Admin    |
| GET    | `/orders/{id}`     | Get order by ID           | Customer/Admin |
| PUT    | `/orders/{id}`     | Update order status       | Admin    |
| DELETE | `/orders/{id}`     | Cancel an order           | Customer |

---

### 🧾 Order Items
| Method | Endpoint                | Description                   | Access   |
|--------|-------------------------|-------------------------------|----------|
| GET    | `/order-items/{id}`     | Get order item by ID          | Customer |
| POST   | `/order-items`          | Add item to an order          | Customer |
| PUT    | `/order-items/{id}`     | Update order item quantity    | Customer |
| DELETE | `/order-items/{id}`     | Remove item from order        | Customer |

---

🔐 **Note:**  
- All **protected endpoints** require a valid JWT token in the `Authorization` header:  

### Setup

```bash
git clone https://github.com/nourhammmad/Book-Store-System.git
cd Book-Store-System
```
### 📂 Project Structure

```
📦 book-store-app
├── 📂 .mvn
│   └── 📂 wrapper
│       └── 📄 maven-wrapper.properties
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java
│   │   │   └── 📂 com
│   │   │       └── 📂 book
│   │   │           └── 📂 store
│   │   │               ├── 📂 Controller
│   │   │               │   ├── 📄 AdminController.java
│   │   │               │   ├── 📄 AuthController.java
│   │   │               │   ├── 📄 BookController.java
│   │   │               │   ├── 📄 CustomerController.java
│   │   │               │   └── 📄 OrderController.java
│   │   │               ├── 📂 Entity
│   │   │               │   ├── 📄 Admin.java
│   │   │               │   ├── 📄 Book.java
│   │   │               │   ├── 📄 BookHistory.java
│   │   │               │   ├── 📄 Customer.java
│   │   │               │   ├── 📄 Order.java
│   │   │               │   ├── 📄 OrderItem.java
│   │   │               │   └── 📄 User.java
│   │   │               ├── 📂 Mapper
│   │   │               │   ├── 📄 AdminMapper.java
│   │   │               │   ├── 📄 BookMapper.java
│   │   │               │   ├── 📄 CustomerMapper.java
│   │   │               │   └── 📄 OrderMapper.java
│   │   │               ├── 📂 Repository
│   │   │               │   ├── 📄 AdminRepository.java
│   │   │               │   ├── 📄 BookHistoryRepository.java
│   │   │               │   ├── 📄 BookRepository.java
│   │   │               │   ├── 📄 CustomerRepository.java
│   │   │               │   ├── 📄 OrderItemRepository.java
│   │   │               │   ├── 📄 OrderRepository.java
│   │   │               │   └── 📄 UserRepository.java
│   │   │               ├── 📂 Seed
│   │   │               │   └── 📄 DataSeeder.java
│   │   │               ├── 📂 Service
│   │   │               │   ├── 📄 AdminService.java
│   │   │               │   ├── 📄 AuthService.java
│   │   │               │   ├── 📄 AuthServiceImpl.java
│   │   │               │   ├── 📄 BookHistoryService.java
│   │   │               │   ├── 📄 BookService.java
│   │   │               │   ├── 📄 CustomerService.java
│   │   │               │   ├── 📄 JwtService.java
│   │   │               │   ├── 📄 JwtServiceImpl.java
│   │   │               │   ├── 📄 OrderService.java
│   │   │               │   └── 📄 UserDetailsServiceImpl.java
│   │   │               ├── 📂 config
│   │   │               │   ├── 📄 JwtAuthenticationFilter.java
│   │   │               │   └── 📄 SecurityConfig.java
│   │   │               ├── 📂 exception
│   │   │               │   ├── 📂 response
│   │   │               │   │   ├── 📄 ErrorDetails.java
│   │   │               │   │   ├── 📄 ValidationFailedResponse.java
│   │   │               │   │   └── 📄 ViolationErrors.java
│   │   │               │   └── 📄 MainExceptionHandler.java
│   │   │               ├── 📂 security
│   │   │               │   └── 📄 CustomUserDetails.java
│   │   │               └── 📄 BookStoreApplication.java
│   │   └── 📂 resources
│   │       └── 📄 application.properties
│   └── 📂 test
│       └── 📂 java
│           └── 📂 com
│               └── 📂 book
│                   └── 📂 store
│                       ├── 📂 Service
│                       │   ├── 📄 BookServiceTest.java
│                       │   ├── 📄 CustomerServiceTest.java
│                       │   └── 📄 OrderServiceTest.java
│                       └── 📄 BookStoreApplicationTests.java
├── 📄 .gitattributes
├── 📄 .gitignore
├── 📄 contract.yml
├── 📄 mvnw
├── 📄 mvnw.cmd
└── 📄 pom.xml
```
